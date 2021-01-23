package com.crawler.parser;

import java.net.URI;
import java.util.List;

public interface LinkParser {
    List<URI> parseLinks(String body);
}
