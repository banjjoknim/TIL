plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
}

group = "com.banjjoknim"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
