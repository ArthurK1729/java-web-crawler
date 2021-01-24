package com.crawler;

import com.crawler.client.ReliabilityConfig;
import com.crawler.client.UnirestClient;
import com.crawler.parser.JsoupAnchorLinkParser;
import com.crawler.validator.SameDomainLinkPolicy;
import com.crawler.webcrawler.WebCrawler;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    public static void main(String[] args) throws Exception {
        var visitedPaths = new ConcurrentHashMap<String, URI>();
        var pathQueue = new ConcurrentLinkedQueue<URI>();
        pathQueue.add(new URI("https://www.baeldung.com/java-concurrent-map"));


        ExecutorService executorService =
                Executors.newFixedThreadPool(4);


        // TODO: static factory?
        // TODO: proper exception handling
        // TODO: performance testing
        // TODO: add ExecutorService and multithreading and the rest
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
    }
}
