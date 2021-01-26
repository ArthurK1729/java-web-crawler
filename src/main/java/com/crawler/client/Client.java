package com.crawler.client;

import java.net.URI;

/** Fetches the html body from the provided link */
public interface Client {
    String fetchBody(URI url);
}
