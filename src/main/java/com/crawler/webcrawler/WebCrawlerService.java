package com.crawler.webcrawler;

import com.crawler.Config;
import com.crawler.client.ReliabilityConfig;
import com.crawler.client.UnirestClient;
import com.crawler.parser.JsoupAnchorLinkParser;
import com.crawler.sink.LinkSink;
import com.crawler.validator.SameDomainLinkPolicy;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main execution loop for the crawler */
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

    public void run(Config config) throws InterruptedException {
        logger.info("Starting web crawler service");

        enqueueUnseenLinks(List.of(config.getStartingLink()));

        var crawler = buildCrawlerFromConfig(config);

        while (true) {
            var originLink = popLink();

            if (originLink.isEmpty()) {
                logger.info("No new paths to explore for a while. Shutting down...");
                break;
            }

            executorService.submit(() -> dispatchCrawler(crawler, originLink.get()));

            throttleRespectfully(config.getThrottleMillis());
        }
    }

    /** Enables respectful scraping */
    private void throttleRespectfully(int throttleMs) throws InterruptedException {
        Thread.sleep(throttleMs);
    }

    private Optional<URI> popLink() throws InterruptedException {
        return Optional.ofNullable(pathQueue.poll(5, TimeUnit.SECONDS));
    }

    private void dispatchCrawler(WebCrawler crawler, URI originLink) {
        var newLinks = crawler.crawl(originLink);

        registerPathAsSeen(originLink);
        enqueueUnseenLinks(newLinks);

        sink.send(newLinks);
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
