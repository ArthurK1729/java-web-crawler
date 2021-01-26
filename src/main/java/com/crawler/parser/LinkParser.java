package com.crawler.parser;

import java.net.URI;
import java.util.List;

/** Parses links from the supplied HTML string */
public interface LinkParser {
    List<URI> parseLinks(String body);
}
