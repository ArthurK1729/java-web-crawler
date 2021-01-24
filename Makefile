.PHONY = lint test clean-build

lint:
	./gradlew spotlessApply

test:
	./gradlew test

clean-build:
	./gradlew clean shadowJar