package com.crawler.webcrawler;

import com.crawler.client.Client;
import com.crawler.parser.LinkParser;
import com.crawler.validator.LinkSelectionPolicy;
import lombok.Builder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

@Builder
public class WebCrawler {
    private final Client client;
    private final LinkParser parser;
    private final List<LinkSelectionPolicy> linkPolicies;
    private final Map<String, URI> visitedPaths;
    private final Queue<URI> pathQueue;

    public void crawl() {
        var startingLink = pathQueue.poll();
        var body = client.fetchBody(startingLink);
        var links = parser.parseLinks(body);
        var validLinks =
                links.stream()
                        .filter(link -> isAllPoliciesPass(startingLink, link))
                        .filter(link -> !isPreviouslyVisitedPath(visitedPaths, link.getPath()))
                        .collect(Collectors.toList());

        System.out.println(links);
        System.out.println(validLinks);

        visitedPaths.put(startingLink.getHost(), startingLink);
    }

    private boolean isAllPoliciesPass(URI startingLink, URI link) {
        for (var policy : linkPolicies) {
            if (!policy.isValidLink(startingLink, link)) return false;
        }

        return true;
    }

    private boolean isPreviouslyVisitedPath(Map<String, URI> visitedPaths, String path) {
        return visitedPaths.containsKey(path);
    }
}
