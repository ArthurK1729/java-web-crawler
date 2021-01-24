package com.crawler.validator;

import java.net.URI;

public class SameDomainLinkPolicy implements LinkSelectionPolicy {
    @Override
    public boolean isValidLink(URI currentUri, URI uri) {
        return currentUri.getHost().equalsIgnoreCase(uri.getHost());
    }
}
