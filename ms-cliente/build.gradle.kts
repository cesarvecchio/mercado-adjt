//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    //kotlin("jvm") version "1.6.20"
}

group = "br.com.cadastrocliente"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testImplementation("org.assertj:assertj-core:3.24.2")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    annotationProcessor("org.projectlombok:lombok")

    /*implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))*/

    testImplementation("io.qameta.allure:allure-junit5:2.23.0")
    implementation("io.qameta.allure:allure-rest-assured:2.23.0")

    testImplementation("io.rest-assured:json-schema-validator:5.3.1")
    testImplementation("io.rest-assured:rest-assured:5.3.0") {
        exclude(group = "org.codehaus.groovy", module = "groovy")
        exclude(group = "org.codehaus.groovy", module = "groovy-xml")
    }

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit-vintage-engine") // Exclui o JUnit 4, se necessário
    }

    testImplementation("io.cucumber:cucumber-java:7.13.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.13.0")
    testImplementation("org.junit.platform:junit-platform-suite-api:1.9.3")

}



tasks.withType<Test> {
    useJUnitPlatform()
}

/*
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    destinationDir = file("$buildDir/target")
}

tasks {
    jar {
        dependsOn("compileTestKotlin")
    }
}

tasks.jar {
    dependsOn("compileTestKotlin")
}

tasks.inspectClassesForKotlinIC {
    dependsOn("compileTestKotlin")
}

tasks.bootJar {
    dependsOn("compileTestKotlin")
}

tasks.resolveMainClassName {
    dependsOn("compileTestKotlin")
}*/
