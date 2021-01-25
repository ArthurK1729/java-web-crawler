package com.crawler.webcrawler;

import com.crawler.client.Client;
import com.crawler.parser.LinkParser;
import com.crawler.validator.LinkSelectionPolicy;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main WebCrawler logic. Knows how to crawl a URI and return a list of valid links */
@Builder
public class WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class.getName());

    private final Client client;
    private final LinkParser parser;
    private final List<LinkSelectionPolicy> linkPolicies;

    public List<URI> crawl(URI startingLink, final Map<String, URI> visitedPaths) {
        logger.info("Crawling {}", startingLink.toString());

        var body = client.fetchBody(startingLink);
        var links = parser.parseLinks(body);

        return links.stream()
                .filter(link -> isAllPoliciesPass(startingLink, link))
                .filter(link -> !isPreviouslyVisitedPath(visitedPaths, link.getPath()))
                .collect(Collectors.toList());
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
