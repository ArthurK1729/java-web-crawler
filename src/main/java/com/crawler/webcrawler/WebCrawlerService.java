package com.crawler.webcrawler;

import com.crawler.Config;
import com.crawler.client.ReliabilityConfig;
import com.crawler.client.UnirestClient;
import com.crawler.parser.JsoupAnchorLinkParser;
import com.crawler.sink.LinkSink;
import com.crawler.validator.SameDomainLinkPolicy;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Builder
public class WebCrawlerService {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawlerService.class.getName());

    private final ExecutorService executorService;
    private final Map<String, URI> visitedPaths;
    private final BlockingQueue<URI> pathQueue;
    private final LinkSink sink;

    public WebCrawlerService(
            ExecutorService executorService,
            Map<String, URI> visitedPaths,
            BlockingQueue<URI> pathQueue,
            LinkSink sink) {
        this.executorService = executorService;
        this.visitedPaths = visitedPaths;
        this.pathQueue = pathQueue;
        this.sink = sink;
    }

    public void run(Config config) {
        logger.info("Starting web crawler service");

        enqueueUnseenLinks(List.of(config.getStartingLink()));

        Callable<Void> crawlerTask =
                () -> {
                    var crawler = buildCrawlerFromConfig(config);

                    logger.info("Crawler operational");

                    while (true) {
                        var originLink = pathQueue.poll(10, TimeUnit.SECONDS);

                        if (originLink == null) {
                            logger.info("No new paths to explore for a while. Shutting down...");
                            return null;
                        }

                        var newLinks = crawler.crawl(originLink);

                        registerPathAsSeen(originLink);
                        enqueueUnseenLinks(newLinks);

                        sink.send(newLinks);

                        //noinspection BusyWait
                        Thread.sleep(config.getThrottleMillis());
                    }
                };

        for (int i = 0; i < config.getConcurrencyLevel(); i++) {
            executorService.submit(crawlerTask);
        }
    }

    private WebCrawler buildCrawlerFromConfig(Config config) {
        return WebCrawler.builder()
                .client(
                        UnirestClient.fromConfig(
                                ReliabilityConfig.builder()
                                        .connectionTimeoutMillis(
                                                config.getConnectionTimeoutMillis())
                                        .withRetries(config.isWithRetries())
                                        .build()))
                .parser(JsoupAnchorLinkParser.newInstance())
                .linkPolicies(List.of(new SameDomainLinkPolicy()))
                .visitedPaths(Collections.unmodifiableMap(visitedPaths))
                .build();
    }

    private void enqueueUnseenLinks(List<URI> links) {
        pathQueue.addAll(links);
    }

    private void registerPathAsSeen(URI originLink) {
        visitedPaths.put(originLink.getPath(), originLink);
    }
}
