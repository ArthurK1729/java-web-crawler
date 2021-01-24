package com.crawler.webcrawler;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;

import com.crawler.TestUtils;
import com.crawler.client.ReliabilityConfig;
import com.crawler.client.UnirestClient;
import com.crawler.parser.JsoupAnchorLinkParser;
import com.crawler.validator.SameDomainLinkPolicy;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestWebCrawlerIntegration {
    // TODO: 80 might be taken. Make crawler work for arbitrary ports
    WireMockServer wireMockServer = new WireMockServer(80);

    @BeforeEach
    public void setup() {
        var htmlBody = TestUtils.readTestResource("base_local.html");
        wireMockServer.stubFor(
                get("/").willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "text/html")
                                        .withStatus(200)
                                        .withBody(htmlBody)));
        wireMockServer.start();
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
        wireMockServer.resetAll();
    }

    @Test
    public void testWebCrawlerFetchesCorrectLinks() throws URISyntaxException {
        var visitedPaths = new HashMap<String, URI>();
        var pathQueue = new LinkedBlockingQueue<URI>();

        var crawler =
                WebCrawler.builder()
                        .client(
                                UnirestClient.fromConfig(
                                        ReliabilityConfig.builder()
                                                .connectionTimeoutMillis(500)
                                                .withRetries(true)
                                                .build()))
                        .parser(JsoupAnchorLinkParser.newInstance())
                        .linkPolicies(List.of(new SameDomainLinkPolicy()))
                        .visitedPaths(visitedPaths)
                        .pathQueue(pathQueue)
                        .build();

        var links = crawler.crawl(new URI("http://localhost"));
        var expectedLinks = List.of(new URI("http://localhost/1"), new URI("http://localhost/2"));

        assertThat(links).isEqualTo(expectedLinks);
    }
}
