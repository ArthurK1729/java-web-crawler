package com.crawler.exceptions;

public class WebCrawlerArgumentException extends RuntimeException {
    public WebCrawlerArgumentException() {
        super("Supplied arguments cannot be parsed");
    }
}
