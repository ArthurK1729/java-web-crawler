package com.crawler;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.Getter;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParserException;

/** Configuration class that knows how to parse command line arguments */
@Getter
public class Config {
    private final URI startingLink;
    private final boolean withRetries;
    private final int connectionTimeoutMillis;
    private final int throttleMillis;
    private final int concurrencyLevel;

    private Config(
            URI startingLink,
            boolean withRetries,
            int connectionTimeoutMillis,
            int throttleMillis,
            int concurrencyLevel) {
        this.startingLink = startingLink;
        this.withRetries = withRetries;
        this.connectionTimeoutMillis = connectionTimeoutMillis;
        this.throttleMillis = throttleMillis;
        this.concurrencyLevel = concurrencyLevel;
    }

    public static Config fromArgs(String[] args) {
        var parser = ArgumentParsers.newFor("webcrawler").build();

        parser.addArgument("--startingLink")
                .help("The seed link to begin the crawling process")
                .type(String.class);

        parser.addArgument("--timeoutMs")
                .help("Specify number of milliseconds to wait before timing out a connection")
                .type(Integer.class)
                .setDefault(1000);

        parser.addArgument("--throttleMs")
                .help(
                        "Specify number of milliseconds to wait before crawling. Can help with polite scraping")
                .type(Integer.class)
                .setDefault(1000);

        parser.addArgument("--withRetries")
                .help("Specify to retry failed connections")
                .action(Arguments.storeTrue())
                .setDefault(false);

        parser.addArgument("--concurrencyLevel")
                .help("Specify number of crawler threads to spin up")
                .type(Integer.class)
                .setDefault(1);

        try {
            var ns = parser.parseArgs(args);

            return new Config(
                    new URI(ns.getString("startingLink")),
                    ns.getBoolean("withRetries"),
                    ns.getInt("timeoutMs"),
                    ns.getInt("throttleMs"),
                    ns.getInt("concurrencyLevel"));
        } catch (ArgumentParserException | URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot parse arguments");
        }
    }
}
