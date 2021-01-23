package com.crawler.webcrawler;

import com.crawler.client.Client;
import com.crawler.parser.LinkParser;
import com.crawler.validator.LinkSelectionPolicy;
import lombok.Builder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public class WebCrawler {
    private final Client client;
    private final LinkParser parser;
    private final List<LinkSelectionPolicy> linkPolicies;

    public void crawl(URI startingPoint) {
        var body = client.fetchBody(startingPoint);
        var links = parser.parseLinks(body);
        var validLinks = links.stream().filter(
                link -> {
                    for(var policy: linkPolicies) {
                        if(!policy.isValidLink(startingPoint, link)) return false;
                    }

                    return true;
                }
        ).collect(Collectors.toList());

        System.out.println(links);
        System.out.println(validLinks);
    }
}
