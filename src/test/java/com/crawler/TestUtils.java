package com.crawler;

import java.nio.file.Files;
import java.nio.file.Path;

public class TestUtils {
    public static String readTestResource(String resourceName) {
        try {
            var classLoader = TestUtils.class.getClassLoader();
            var testFileLocation = Path.of(classLoader.getResource(resourceName).toURI());
            return Files.readString(testFileLocation);
        } catch (Exception e) {
            throw new RuntimeException("Unable to load test resource");
        }
    }
}
