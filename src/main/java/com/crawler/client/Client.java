package com.crawler.client;

import java.net.URI;

public interface Client {
    String fetchBody(URI url);
}
