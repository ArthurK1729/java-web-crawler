package com.crawler;

import com.crawler.client.ReliabilityConfig;
import com.crawler.client.UnirestClient;
import com.crawler.parser.JsoupAnchorLinkParser;
import com.crawler.validator.SameDomainLinkPolicy;
import com.crawler.webcrawler.WebCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Application {
    private static Logger logger = LoggerFactory.getLogger(Application.class.getName());

    // TODO: static factory?
    // TODO: proper exception handling
    // TODO: performance testing
    // TODO: put this in Argparse4j
    // TODO: finish configuring log4j
    public static void main(String[] args) throws Exception {
        logger.info("Starting web crawler");

        var concurrencyLevel = 128;
        var startingLink = "https://www.baeldung.com/java-concurrent-map";

        var visitedPaths = new ConcurrentHashMap<String, URI>();
        var pathQueue = new ConcurrentLinkedQueue<URI>();
        pathQueue.add(new URI(startingLink));

        Runnable crawlingTask =
                () -> {
                    var crawler =
                            WebCrawler.builder()
                                    .client(
                                            UnirestClient.fromConfig(
                                                    ReliabilityConfig.builder()
                                                            .connectionTimeoutMillis(500)
                                                            .build()))
                                    .parser(JsoupAnchorLinkParser.newInstance())
                                    .linkPolicies(List.of(SameDomainLinkPolicy.newInstance()))
                                    .visitedPaths(visitedPaths)
                                    .pathQueue(pathQueue)
                                    .build();

                    crawler.crawl();
                };

        ExecutorService executorService = getExecutorService(concurrencyLevel);

        for (int i = 0; i < concurrencyLevel; i++) {
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
