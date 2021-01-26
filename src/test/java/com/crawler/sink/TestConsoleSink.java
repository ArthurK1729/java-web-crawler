package com.crawler.sink;

import static java.util.List.of;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestConsoleSink {

    @Mock PrintStream stream;

    @Test
    public void testNonEmptyLinksPrinted() throws URISyntaxException {
        var sink = new ConsoleSink(stream);
        var expectedLinks =
                of(new URI("https://www.example.com/1"), new URI("https://www.example.com/2"));

        sink.send(expectedLinks);

        verify(stream).println(expectedLinks);
    }

    @Test
    public void testEmptyLinksNotPrinted() throws URISyntaxException {
        var sink = new ConsoleSink(stream);
        List<URI> expectedLinks = List.of();

        sink.send(expectedLinks);

        verify(stream, never()).println();
    }
}
