.PHONY = lint test clean build-jar coverage-report

lint:
	./gradlew spotlessApply

clean:
	./gradlew clean
	rm -rf ./out

test:
	./gradlew test

build-jar: clean test
	./gradlew clean shadowJar

coverage-report:
	./gradlew jacocoTestReport