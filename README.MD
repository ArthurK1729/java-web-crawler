# Java Web Crawler
This is a Java implementation of a simple web crawler.
### Getting Started
1. Install lombok plugin in Intellij
2. Install java 11

### How to run crawler
```shell script
make build-jar
java -jar ./build/libs/java-web-crawler-0.0.1-SNAPSHOT-all.jar --startingLink https://www.baeldung.com/introduction-to-wiremock --withRetries --timeoutMs 500 --throttleMs 1000 --concurrencyLevel 8
```
