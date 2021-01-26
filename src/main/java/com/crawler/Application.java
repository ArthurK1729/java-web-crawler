package com.crawler;

import com.crawler.client.ReliabilityConfig;
import com.crawler.client.UnirestClient;
import com.crawler.parser.JsoupAnchorLinkParser;
import com.crawler.sink.ConsoleSink;
import com.crawler.sink.LinkSink;
import com.crawler.validator.SameDomainLinkPolicy;
import com.crawler.webcrawler.WebCrawlerTask;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: too much logic in entrypoint class...
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class.getName());

    private static final Map<String, URI> visitedPaths = new ConcurrentHashMap<>();
    private static final BlockingQueue<URI> pathQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        logger.info("Starting web crawler");

        var config = Config.fromArgs(args).orElseThrow(IllegalArgumentException::new);
        LinkSink sink = ConsoleSink.getStdOut();

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

        var executorService = getExecutorService(config.getConcurrencyLevel());

        for (int i = 0; i < config.getConcurrencyLevel(); i++) {
            executorService.submit(crawlerTask);
        }
    }

    private static WebCrawlerTask buildCrawlerFromConfig(Config config) {
        return WebCrawlerTask.builder()
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

    private static void enqueueUnseenLinks(List<URI> links) {
        pathQueue.addAll(links);
    }

    private static void registerPathAsSeen(URI originLink) {
        visitedPaths.put(originLink.getPath(), originLink);
    }

    private static ExecutorService getExecutorService(int concurrencyLevel) {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrencyLevel);
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    logger.info("Shutting down executor service");
                                    executorService.shutdown();
                                }));

        return executorService;
    }
}
