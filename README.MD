# Java Web Crawler
### Getting Started
1. Install lombok plugin
2. Install java 11

### How to run jar
```shell script
make build-jar
java -jar ./build/libs/java-web-crawler-0.0.1-SNAPSHOT-all.jar --startingLink https://www.baeldung.com/introduction-to-wiremock --withRetries --timeoutMs 500 --concurrencyLevel 8
```
