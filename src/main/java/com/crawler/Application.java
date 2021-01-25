package com.crawler;

import com.crawler.client.ReliabilityConfig;
import com.crawler.client.UnirestClient;
import com.crawler.parser.JsoupAnchorLinkParser;
import com.crawler.validator.SameDomainLinkPolicy;
import com.crawler.webcrawler.WebCrawler;
import java.net.URI;
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

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class.getName());

    private static final Map<String, URI> visitedPaths = new ConcurrentHashMap<>();
    private static final BlockingQueue<URI> pathQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        logger.info("Starting web crawler");
        var config = Config.fromArgs(args).orElseThrow(IllegalArgumentException::new);

        pathQueue.add(config.getStartingLink());

        Callable<Void> crawlerTask =
                () -> {
                    var crawler = buildCrawlerFromConfig(config);

                    logger.info("Crawler operational");

                    while (true) {
                        var originLink = pathQueue.poll(10, TimeUnit.SECONDS);

                        if (originLink == null) {
                            logger.info("No new paths to explore for a while. Shutting down...");
                            break;
                        }

                        var newLinks = crawler.crawl(originLink, visitedPaths);

                        registerPathAsSeen(originLink);
                        registerUnseenLinks(newLinks);

                        logger.info("Discovered links {}", newLinks);

                        //noinspection BusyWait
                        Thread.sleep(config.getThrottleMillis());
                    }

                    return null;
                };

        var executorService = getExecutorService(config.getConcurrencyLevel());

        for (int i = 0; i < config.getConcurrencyLevel(); i++) {
            executorService.submit(crawlerTask);
        }
    }

    private static WebCrawler buildCrawlerFromConfig(Config config) {
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
                .build();
    }

    private static void registerUnseenLinks(List<URI> links) {
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
