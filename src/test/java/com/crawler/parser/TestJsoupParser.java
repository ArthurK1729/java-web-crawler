package com.crawler.parser;

import com.crawler.TestUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestJsoupParser {
    String testBody = TestUtils.readTestResource("base.html");

    @Test
    public void testCorrectNumberOfAnchorsParsed() {
        var parser = JsoupAnchorLinkParser.newInstance();

        var parsedAnchors = parser.parseLinks(testBody);

        assertThat(parsedAnchors).hasSize(3);
    }

    @Test
    public void testAllParsedAnchorsAreAbsolute() {
        var parser = JsoupAnchorLinkParser.newInstance();

        var parsedAnchors = parser.parseLinks(testBody);

        assertThat(parsedAnchors)
                .allSatisfy(link -> assertThat(link.toString()).startsWith("https://"));
    }
}
