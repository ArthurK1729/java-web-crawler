package com.crawler;

import com.crawler.client.ReliabilityConfig;
import com.crawler.client.UnirestClient;
import com.crawler.parser.JsoupAnchorLinkParser;
import com.crawler.validator.SameDomainLinkPolicy;
import com.crawler.webcrawler.WebCrawler;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class.getName());

    // TODO: proper exception handling
    // TODO: performance testing
    public static void main(String[] args) {
        logger.info("Starting web crawler");
        var config = Config.fromArgs(args);

        var visitedPaths = new ConcurrentHashMap<String, URI>();
        var pathQueue = new LinkedBlockingQueue<URI>();
        pathQueue.add(config.getStartingLink());

        Runnable crawlingTask =
                () -> {
                    var crawler =
                            WebCrawler.builder()
                                    .client(
                                            UnirestClient.fromConfig(
                                                    ReliabilityConfig.builder()
                                                            .connectionTimeoutMillis(
                                                                    config
                                                                            .getConnectionTimeoutMillis())
                                                            .withRetries(config.isWithRetries())
                                                            .build()))
                                    .parser(JsoupAnchorLinkParser.newInstance())
                                    .linkPolicies(List.of(new SameDomainLinkPolicy()))
                                    .visitedPaths(visitedPaths)
                                    .pathQueue(pathQueue)
                                    .build();

                    logger.info("Crawler operational");

                    //noinspection InfiniteLoopStatement
                    while (true) {
                        try {
                            var startingLink = pathQueue.take();
                            var links = crawler.crawl(startingLink);

                            logger.info("Discovered links {}", links);

                            Thread.sleep(config.getThrottleMillis());
                        } catch (InterruptedException ignored) {
                        }
                    }
                };

        ExecutorService executorService = getExecutorService(config.getConcurrencyLevel());

        for (int i = 0; i < config.getConcurrencyLevel(); i++) {
            executorService.submit(crawlingTask);
        }
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
