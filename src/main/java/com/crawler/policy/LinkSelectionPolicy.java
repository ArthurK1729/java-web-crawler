package com.crawler.policy;

import com.crawler.webcrawler.Context;
import java.net.URI;

/** Determines if URI is valid given the crawling context */
public interface LinkSelectionPolicy {
    boolean isValidLink(Context currentContext, URI uri);
}
