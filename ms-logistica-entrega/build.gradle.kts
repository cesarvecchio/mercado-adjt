plugins {
    java
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("io.gatling.gradle") version "3.10.5"
}

group = "org.example"
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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.gatling:gatling-app:3.9.5")
    implementation("io.gatling.highcharts:gatling-charts-highcharts:3.9.5")
    implementation("io.qameta.allure:allure-rest-assured:2.23.0")

    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.1")
    implementation("org.springframework.cloud:spring-cloud-dependencies:2023.0.1")

    implementation("org.springframework.boot:spring-boot-starter-mail")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    //developmentOnly("org.springframework.boot:spring-boot-devtools")

//    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.rest-assured:json-schema-validator:5.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.apache.commons:commons-compress:1.26.1")
    testImplementation("io.cucumber:cucumber-java:7.13.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.13.0")
    testImplementation("org.junit.platform:junit-platform-suite-api:1.9.3")
    testImplementation("io.qameta.allure:allure-junit5:2.23.0")


}

tasks.withType<Test> {
    useJUnitPlatform()
}
