.PHONY = lint test build-jar coverage-report

lint:
	./gradlew spotlessApply

test:
	./gradlew test

build-jar: test
	./gradlew clean shadowJar

coverage-report:
	./gradlew jacocoTestReport