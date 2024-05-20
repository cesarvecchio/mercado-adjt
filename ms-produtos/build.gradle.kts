plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "br.com"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

val cucumberRuntime: Configuration by configurations.creating {
	extendsFrom(configurations["testImplementation"])
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-batch")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")


	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.batch:spring-batch-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	testImplementation("io.rest-assured:rest-assured:5.3.0") {
		exclude(group = "org.codehaus.groovy", module = "groovy")
		exclude(group = "org.codehaus.groovy", module = "groovy-xml")
	}
	testImplementation("io.rest-assured:json-schema-validator:5.3.1")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
	testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
	testImplementation("org.assertj:assertj-core:3.24.2")
	testImplementation("org.apache.commons:commons-compress:1.26.1")

	testImplementation("io.cucumber:cucumber-java:7.13.0")
	testImplementation("io.cucumber:cucumber-junit-platform-engine:7.13.0")
	testImplementation("org.junit.platform:junit-platform-suite-api:1.9.3")

	testImplementation("io.qameta.allure:allure-junit5:2.23.0")
	implementation("io.qameta.allure:allure-rest-assured:2.23.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
	testLogging {
		events("passed", "skipped", "failed")
	}
}

tasks.register<Test>("unitTest") {
	filter {
		includeTestsMatching("br.com.msprodutos.*Test")
	}
}

tasks.register<Test>("integrationTest") {
	filter {
		includeTestsMatching("br.com.msprodutos.*IT")
	}
}

tasks.register("cucumberCli") {
	dependsOn("assemble", "testClasses")
	doLast {
		javaexec {
			mainClass.set("io.cucumber.core.cli.Main")
			classpath = cucumberRuntime + sourceSets.main.get().output + sourceSets.test.get().output
			args = listOf(
					"--plugin", "pretty",
					"--plugin", "html:build/cucumber-reports/cucumber.html",
					"--glue", "br.com.msprodutos.bdd",
					"src/test/resources")
		}
	}
}
