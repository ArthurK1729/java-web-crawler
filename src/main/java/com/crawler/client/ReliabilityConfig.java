package com.crawler.client;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/** Configures the client used to fetch html bodies */
@ToString
@Builder
@Getter
public class ReliabilityConfig {
    private final int connectionTimeoutMillis;
    private final boolean withRetries;
}
