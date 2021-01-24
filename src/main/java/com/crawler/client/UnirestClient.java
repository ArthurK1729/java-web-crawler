package com.crawler.client;

import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

import java.net.URI;

public class UnirestClient implements Client {
    private final UnirestInstance client;

    private UnirestClient(ReliabilityConfig reliabilityConfig) {
        var config =
                Unirest.config().connectTimeout(reliabilityConfig.getConnectionTimeoutMillis());
        client = new UnirestInstance(config);
    }

    @Override
    public String fetchBody(URI uri) {
        // TODO: exception handling
        // TODO: execute javascript?
        return client.get(uri.toString()).asString().getBody();
    }

    public static UnirestClient fromConfig(ReliabilityConfig config) {
        return new UnirestClient(config);
    }
}
