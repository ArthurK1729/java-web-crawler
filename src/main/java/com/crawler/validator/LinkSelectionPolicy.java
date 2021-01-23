package com.crawler.validator;

import java.net.URL;

public interface LinkSelectionPolicy {
    // TODO: change to some kind of context object
    boolean isValidLink(URL currentUrl, URL url);
}
