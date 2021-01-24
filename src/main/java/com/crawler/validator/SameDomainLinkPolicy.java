package com.crawler.validator;

import java.net.URI;

/** The link policy that tells whether or not the provided link is from the same domain */
public class SameDomainLinkPolicy implements LinkSelectionPolicy {
    @Override
    public boolean isValidLink(URI currentUri, URI uri) {
        return currentUri.getHost().equalsIgnoreCase(uri.getHost());
    }
}
