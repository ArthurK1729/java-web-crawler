package com.crawler.client;

import java.net.URI;
import java.util.Optional;

public interface Client {
    String fetchBody(URI url);
}
