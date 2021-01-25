package com.crawler.webcrawler;

import java.net.URI;
import lombok.Builder;
import lombok.Getter;

/** Stores the current crawling context. Used for various decision making */
@Getter
@Builder
public class Context {
    private final URI currentUri;
}
