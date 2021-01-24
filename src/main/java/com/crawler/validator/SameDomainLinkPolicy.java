package com.crawler.validator;

import java.net.URI;

public class SameDomainLinkPolicy implements LinkSelectionPolicy {

    private SameDomainLinkPolicy() {}

    @Override
    public boolean isValidLink(URI currentUri, URI uri) {
        return currentUri.getHost().equalsIgnoreCase(uri.getHost());
    }

    public static SameDomainLinkPolicy newInstance() {
        return new SameDomainLinkPolicy();
    }
}
