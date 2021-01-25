package com.crawler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class TestUtils {
    public static String readTestResource(String resourceName) {
        try {
            var classLoader = TestUtils.class.getClassLoader();
            var testFileLocation =
                    Path.of(Objects.requireNonNull(classLoader.getResource(resourceName)).toURI());
            return Files.readString(testFileLocation);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load test resource " + resourceName);
        }
    }
}
