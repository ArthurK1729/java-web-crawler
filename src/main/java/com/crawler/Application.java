package com.crawler;

import com.crawler.sink.ConsoleSink;
import com.crawler.webcrawler.WebCrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class.getName());

    public static void main(String[] args) {
        logger.info("Starting web crawler");

        var config = Config.fromArgs(args).orElseThrow(IllegalArgumentException::new);

        var service =
                WebCrawlerService.builder()
                        .executorService(getExecutorService(config.getConcurrencyLevel()))
                        .pathQueue(new LinkedBlockingQueue<>())
                        .visitedPaths(new ConcurrentHashMap<>())
                        .sink(ConsoleSink.getStdOut())
                        .build();

        service.run(config);
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
