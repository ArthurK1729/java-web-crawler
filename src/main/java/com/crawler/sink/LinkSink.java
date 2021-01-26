package com.crawler.sink;

import java.net.URI;
import java.util.List;

public interface LinkSink {
    void send(List<URI> links);
}
