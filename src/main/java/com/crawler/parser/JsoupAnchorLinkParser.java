package com.crawler.parser;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;

// TODO: add support for relative links
/** A link parser implementation that is limited to collecting <a> tags. Ignores relative links */
public class JsoupAnchorLinkParser implements LinkParser {
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
