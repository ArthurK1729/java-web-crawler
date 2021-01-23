package com.crawler;

import com.crawler.client.ReliabilityConfig;
import com.crawler.client.UnirestClient;
import com.crawler.parser.JsoupLinkParser;
import com.crawler.webcrawler.WebCrawler;

import java.net.URI;

public class Application {

    public static void main(String[] args) throws Exception {

        // TODO: static factory?
        // TODO: proper exception handling
        // TODO: performance testing
        var crawler =
                WebCrawler.builder()
                        .client(
                                new UnirestClient(
                                        ReliabilityConfig.builder()
                                                .connectionTimeoutMillis(100)
                                                .build()))
                        .parser(new JsoupLinkParser())
                        .build();

        crawler.crawl(new URI("https://www.google.com"));
    }
}
