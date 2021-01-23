package com.crawler.client;

import kong.unirest.Unirest;

import java.net.URI;

public class UnirestClient implements Client {
    public UnirestClient(ReliabilityConfig reliabilityConfig) {
        Unirest.config().connectTimeout(reliabilityConfig.getConnectionTimeoutMillis());
    }

    @Override
    public String fetchBody(URI uri) {
        // TODO: exception handling
        return Unirest.get(uri.toString()).asString().getBody();
    }
}
