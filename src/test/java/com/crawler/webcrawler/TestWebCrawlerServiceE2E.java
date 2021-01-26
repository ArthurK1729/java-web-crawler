package com.crawler.webcrawler;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

import com.crawler.Config;
import com.crawler.TestUtils;
import com.crawler.sink.MockSink;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("e2e")
public class TestWebCrawlerServiceE2E {
    WireMockServer wireMockServer = new WireMockServer(80);

    @BeforeEach
    public void setup() {
        var htmlBody = TestUtils.readTestResource("base_local.html");
        var emptyHtmlBody = TestUtils.readTestResource("empty.html");

        wireMockServer.stubFor(
                get("/").willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "text/html")
                                        .withStatus(200)
                                        .withBody(htmlBody)));

        wireMockServer.stubFor(
                get(urlPathMatching("/[12]"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "text/html")
                                        .withStatus(200)
                                        .withBody(emptyHtmlBody)));
        wireMockServer.start();
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
        wireMockServer.resetAll();
    }

    @Test
    public void testPrintsOriginAndDiscoveredLinks()
            throws InterruptedException, URISyntaxException {
        var executorService = Executors.newSingleThreadExecutor();
        var pathQueue = new LinkedBlockingDeque<URI>();
        var visitedPaths = new HashMap<String, URI>();
        var mockedSink = new MockSink();
        var expectedLinks =
                List.of(
                        new URI("http://localhost"),
                        new URI("http://localhost/1"),
                        new URI("http://localhost/2"),
                        new URI("http://localhost/1"),
                        new URI("http://localhost/2"));

        var args = new String[] {"--startingLink", "http://localhost"};

        var config = Config.fromArgs(args).get();

        var service =
                WebCrawlerService.builder()
                        .executorService(executorService)
                        .pathQueue(pathQueue)
                        .visitedPaths(visitedPaths)
                        .sink(mockedSink)
                        .build();

        service.run(config);

        assertThat(mockedSink.sentLinks).isEqualTo(expectedLinks);
    }
}
