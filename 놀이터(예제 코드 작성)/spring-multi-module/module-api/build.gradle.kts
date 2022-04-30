plugins {
    /**
     * build.gradle.kts(springmultimodule) 의 subprojects 항목에서 아래의 플러그인을 적용해주고 있으므로 주석처리.
     *
     * id("org.springframework.boot") version "2.6.7"
     * id("io.spring.dependency-management") version "1.0.11.RELEASE"
     */
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.banjjoknim"
version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_11

dependencies {
    implementation(project(":module-domain"))

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
