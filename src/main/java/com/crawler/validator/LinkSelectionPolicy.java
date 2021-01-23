package com.crawler.validator;

import java.net.URI;

public interface LinkSelectionPolicy {
    // TODO: change to some kind of context object
    boolean isValidLink(URI currentUri, URI uri);
}
