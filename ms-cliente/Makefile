build-project:
	gradle build --refresh-dependencies -x test

unit-test: gradle-clean
	gradle unitTest

integration-test: gradle-clean
	gradle integrationTest

system-test:
	gradle cucumberCli

test: unit-test integration-test

gradle-clean:
	gradle clean