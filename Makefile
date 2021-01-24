.PHONY = lint test build-jar

lint:
	./gradlew spotlessApply

test:
	./gradlew test

build-jar: test
	./gradlew clean shadowJar