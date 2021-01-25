package com.crawler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtils {
    private static final Logger logger = LoggerFactory.getLogger(TestUtils.class.getName());

    public static String readTestResource(String resourceName) {
        try {
            var classLoader = TestUtils.class.getClassLoader();
            var testFileLocation =
                    Path.of(Objects.requireNonNull(classLoader.getResource(resourceName)).toURI());
            return Files.readString(testFileLocation);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Unable to load test resource " + resourceName);
        }
    }
}
