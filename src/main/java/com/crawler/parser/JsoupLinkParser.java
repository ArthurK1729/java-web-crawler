package com.crawler.parser;

import org.jsoup.Jsoup;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class JsoupLinkParser implements LinkParser {
    // TODO: some links are relative. Handle that
    // TODO: deal with www prefix for uniqueness
    @Override
    public List<URI> parseLinks(String body) {
        var parsedBody = Jsoup.parse(body);
        var parsedLinks = parsedBody.body().select("a").eachAttr("href");

        return parsedLinks.stream()
                .filter(link -> !(link.startsWith("/") || link.startsWith(".")))
                .map(URI::create)
                .collect(Collectors.toList());
    }
}
