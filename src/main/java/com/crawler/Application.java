package com.crawler;

import com.crawler.client.ReliabilityConfig;
import com.crawler.client.UnirestClient;
import com.crawler.exceptions.WebCrawlerArgumentException;
import com.crawler.parser.JsoupAnchorLinkParser;
import com.crawler.validator.SameDomainLinkPolicy;
import com.crawler.webcrawler.WebCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) {
        logger.info("Starting web crawler");
        var config = Config.fromArgs(args).orElseThrow(WebCrawlerArgumentException::new);

        var visitedPaths = new ConcurrentHashMap<String, URI>();
        var pathQueue = new LinkedBlockingQueue<URI>();
        pathQueue.add(config.getStartingLink());

        Runnable crawlerTask =
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
                            // TODO: remove side effects from crawler code after all...
                            var links = crawler.crawl(startingLink);

                            logger.info("Discovered links {}", links);

                            //noinspection BusyWait
                            Thread.sleep(config.getThrottleMillis());
                        } catch (InterruptedException ignored) {
                        }
                    }
                };

        ExecutorService executorService = getExecutorService(config.getConcurrencyLevel());

        for (int i = 0; i < config.getConcurrencyLevel(); i++) {
            executorService.submit(crawlerTask);
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
