package com.crawler.webcrawler;

import com.crawler.client.Client;
import com.crawler.parser.LinkParser;
import com.crawler.policy.LinkSelectionPolicy;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

/** Main WebCrawler logic. Knows how to crawl a URI and return a list of valid links */
@Builder
class WebCrawler {
    private final Client client;
    private final LinkParser parser;
    private final List<LinkSelectionPolicy> linkPolicies;
    private final Map<String, URI> visitedPaths;

    public List<URI> crawl(URI startingLink) {
        var body = client.fetchBody(startingLink);
        var links = parser.parseLinks(body);

        var currentContext = Context.builder().currentUri(startingLink).build();

        return links.stream()
                .filter(link -> isAllPoliciesPass(currentContext, link))
                .filter(link -> !isPreviouslyVisitedPath(visitedPaths, link.getPath()))
                .collect(Collectors.toList());
    }

    private boolean isAllPoliciesPass(Context currentContext, URI link) {
        for (var policy : linkPolicies) {
            if (!policy.isValidLink(currentContext, link)) return false;
        }

        return true;
    }

    private boolean isPreviouslyVisitedPath(Map<String, URI> visitedPaths, String path) {
        return visitedPaths.containsKey(path);
    }
}
