package com.crawler.sink;

import java.net.URI;
import java.util.List;

/**
 * The place to send links after they are fetched. The sink can be a file, a console, or a network
 * call etc
 */
public interface LinkSink {
    void send(List<URI> links);
}
