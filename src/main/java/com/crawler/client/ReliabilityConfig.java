package com.crawler.client;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
public class ReliabilityConfig {
    private final int connectionTimeoutMillis;
}
