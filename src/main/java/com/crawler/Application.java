package com.crawler;

import com.crawler.sink.ConsoleSink;
import com.crawler.webcrawler.WebCrawlerService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) {
        logger.info("Starting web crawler");

        var config = Config.fromArgs(args).orElseThrow(IllegalArgumentException::new);
        var executorService = getExecutorService(config.getConcurrencyLevel());

        var service =
                WebCrawlerService.builder()
                        .executorService(executorService)
                        .pathQueue(new LinkedBlockingQueue<>())
                        .visitedPaths(new ConcurrentHashMap<>())
                        .sink(ConsoleSink.getStdOut())
                        .build();

        try {
            service.run(config);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            shutdownGracefully(executorService);
        }
    }

    private static ExecutorService getExecutorService(int concurrencyLevel) {
        ExecutorService executorService = Executors.newFixedThreadPool(concurrencyLevel);
        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                () -> {
                                    logger.info("Shutting down executor service");
                                    shutdownGracefully(executorService);
                                }));

        return executorService;
    }

    private static void shutdownGracefully(ExecutorService executorService) {
        try {
            executorService.shutdownNow();
            executorService.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
