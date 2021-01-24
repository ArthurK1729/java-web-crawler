package com.crawler.client;

import java.net.URI;
import kong.unirest.CookieSpecs;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;

public class UnirestClient implements Client {
    private final UnirestInstance client;

    private UnirestClient(ReliabilityConfig reliabilityConfig) {
        var config =
                Unirest.config()
                        .connectTimeout(reliabilityConfig.getConnectionTimeoutMillis())
                        .automaticRetries(reliabilityConfig.isWithRetries())
                        .cookieSpec(CookieSpecs.IGNORE_COOKIES);

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
