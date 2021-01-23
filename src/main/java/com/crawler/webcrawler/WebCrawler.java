package com.crawler.webcrawler;

import com.crawler.client.Client;
import com.crawler.parser.LinkParser;
import lombok.Builder;

import java.net.URI;

@Builder
public class WebCrawler {
    private final Client client;
    private final LinkParser parser;

    public void crawl(URI startingPoint) {
        var body = client.fetchBody(startingPoint);
        var links = parser.parseLinks(body);

        System.out.println(links);

        for(var link: links) {
            System.out.println(link.getHost());
        }
    }
}
