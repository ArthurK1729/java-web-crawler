package com.crawler.validator;

import java.net.URL;

public class SameDomainLinkPolicy implements LinkSelectionPolicy {
    @Override
    public boolean isValidLink(URL currentUrl, URL url) {
        return currentUrl.getHost().equalsIgnoreCase(url.getHost());
    }
}
