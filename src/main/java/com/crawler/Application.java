package com.crawler;

import com.crawler.webcrawler.WebCrawler;
import kong.unirest.Unirest;

import java.net.URL;

public class Application {

    public static void main(String[] args) throws Exception {

        // TODO: static factory?
        // TODO: proper exception handling
        var webCrawler = new WebCrawler();
        webCrawler.crawl(new URL("somelink"));
    }
}
