package com.crawler.sink;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MockSink implements LinkSink {
    public List<URI> sentLinks;

    public MockSink() {
        this.sentLinks = new ArrayList<>();
    }

    @Override
    public void send(List<URI> links) {
        sentLinks.addAll(links);
    }
}
