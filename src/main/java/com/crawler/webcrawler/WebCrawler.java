package com.crawler.webcrawler;

import com.crawler.client.Client;
import com.crawler.parser.LinkParser;
import com.crawler.validator.LinkSelectionPolicy;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

@Builder
public class WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class.getName());

    private final Client client;
    private final LinkParser parser;
    private final List<LinkSelectionPolicy> linkPolicies;
    private final Map<String, URI> visitedPaths;
    private final BlockingQueue<URI> pathQueue;

    public List<URI> crawl(URI startingLink) {
        logger.info("Crawling {}", startingLink.toString());

        var body = client.fetchBody(startingLink);
        var links = parser.parseLinks(body);
        var validLinks =
                links.stream()
                        .filter(link -> isAllPoliciesPass(startingLink, link))
                        .filter(link -> !isPreviouslyVisitedPath(visitedPaths, link.getPath()))
                        .collect(Collectors.toList());

        visitedPaths.put(startingLink.getPath(), startingLink);
        pathQueue.addAll(validLinks);

        return validLinks;
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
