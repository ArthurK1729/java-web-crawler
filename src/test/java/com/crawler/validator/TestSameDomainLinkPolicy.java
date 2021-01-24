package com.crawler.validator;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

public class TestSameDomainLinkPolicy {

    @Test
    public void testValidLinkReturnsTrue() throws URISyntaxException {
        var policy = new SameDomainLinkPolicy();
        var currentLocation = new URI("https://www.example.com");
        var crawlLocation = new URI("https://www.example.com/1");

        assertThat(policy.isValidLink(currentLocation, crawlLocation)).isTrue();
    }

    @Test
    public void testInvalidLinkReturnsFalse() throws URISyntaxException {
        var policy = new SameDomainLinkPolicy();
        var currentLocation = new URI("https://www.example.com");
        var crawlLocation = new URI("https://www.newexample.com/1");

        assertThat(policy.isValidLink(currentLocation, crawlLocation)).isFalse();
    }
}
