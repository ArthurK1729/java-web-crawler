package com.crawler.policy;

import com.crawler.webcrawler.Context;
import java.net.URI;

/** The link policy that tells whether or not the provided link is from the same domain */
public class SameDomainLinkPolicy implements LinkSelectionPolicy {
    @Override
    public boolean isValidLink(Context currentContext, URI uri) {
        return currentContext.getCurrentUri().getHost().equalsIgnoreCase(uri.getHost());
    }
}
