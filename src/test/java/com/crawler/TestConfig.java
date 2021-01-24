package com.crawler;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

// TODO: add some negative tests
public class TestConfig {
    @Test
    public void testArgumentsParsedCorrectly() throws URISyntaxException {
        var args =
                new String[] {
                    "--startingLink",
                    "https://www.baeldung.com/introduction-to-wiremock",
                    "--withRetries",
                    "--timeoutMs",
                    "500",
                    "--throttleMs",
                    "1000",
                    "--concurrencyLevel",
                    "8",
                };

        var config = Config.fromArgs(args);

        assertThat(config.getStartingLink())
                .isEqualTo(new URI("https://www.baeldung.com/introduction-to-wiremock"));
        assertThat(config.isWithRetries()).isEqualTo(true);
        assertThat(config.getConnectionTimeoutMillis()).isEqualTo(500);
        assertThat(config.getThrottleMillis()).isEqualTo(1000);
        assertThat(config.getConcurrencyLevel()).isEqualTo(8);
    }
}
