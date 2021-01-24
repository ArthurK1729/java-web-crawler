package com.crawler.parser;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;

/** A link parser implementation that is limited to collecting <a> tags */
public class JsoupAnchorLinkParser implements LinkParser {
    // TODO: some links are relative. Handle that
    // TODO: deal with www prefix for uniqueness
    // TODO: wiremock integration test

    private JsoupAnchorLinkParser() {}

    @Override
    public List<URI> parseLinks(String body) {
        var parsedBody = Jsoup.parse(body);
        var parsedLinks = parsedBody.body().select("a").eachAttr("href");

        return parsedLinks.stream()
                .filter(link -> !(link.startsWith("/") || link.startsWith(".")))
                .map(URI::create)
                .collect(Collectors.toList());
    }

    public static JsoupAnchorLinkParser newInstance() {
        return new JsoupAnchorLinkParser();
    }
}
