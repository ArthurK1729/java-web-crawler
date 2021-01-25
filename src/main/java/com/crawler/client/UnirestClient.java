package com.crawler.client;

import java.net.URI;
import kong.unirest.CookieSpecs;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Unirest-based implementation of the client. Obtains html bodies from provided links */
public class UnirestClient implements Client {
    private static final Logger logger = LoggerFactory.getLogger(UnirestClient.class.getName());

    private final UnirestInstance client;

    private UnirestClient(ReliabilityConfig reliabilityConfig) {
        logger.info("Initialising Unirest with config {}", reliabilityConfig);

        var config =
                Unirest.config()
                        .connectTimeout(reliabilityConfig.getConnectionTimeoutMillis())
                        .automaticRetries(reliabilityConfig.isWithRetries())
                        .cookieSpec(CookieSpecs.IGNORE_COOKIES);

        client = new UnirestInstance(config);
    }

    @Override
    public String fetchBody(URI uri) {
        // TODO: execute javascript to render more links? (could pose a security risk)
        return client.get(uri.toString()).asString().getBody();
    }

    public static UnirestClient fromConfig(ReliabilityConfig config) {
        return new UnirestClient(config);
    }
}
