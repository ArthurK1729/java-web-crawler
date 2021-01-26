.PHONY = lint test clean build-jar coverage-report test-integration

lint:
	./gradlew spotlessApply

clean:
	./gradlew clean
	rm -rf ./out

test:
	./gradlew test

test-e2e:
	./gradlew e2eTest

build-jar: clean test
	./gradlew clean shadowJar

coverage-report:
	./gradlew jacocoTestReport