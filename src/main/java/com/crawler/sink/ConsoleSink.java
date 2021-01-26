package com.crawler.sink;

import java.io.PrintStream;
import java.net.URI;
import java.util.List;

public class ConsoleSink implements LinkSink {
    private final PrintStream stream;

    public ConsoleSink(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void send(List<URI> links) {
        // TODO: beautify output for readability
        if (!links.isEmpty()) stream.println(links);
    }

    public static ConsoleSink getStdOut() {
        return new ConsoleSink(System.out);
    }
}
