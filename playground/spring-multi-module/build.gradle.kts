plugins {
    id("org.springframework.boot") version "2.6.7" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    kotlin("jvm") version "1.6.21" apply false
    kotlin("plugin.spring") version "1.6.21" apply false
    kotlin("plugin.jpa") version "1.6.21" apply false
}

allprojects { // 모든 프로젝트 모듈에 아래의 사항을 적용한다.
    group = "com.banjjoknim"
    version = "0.0.1-SNAPSHOT"

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

subprojects {
    repositories {
        mavenCentral()
    }

    apply { // subprojects, 서브 모듈들에 아래의 플러그인들을 적용한다.
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
    }
}

dependencies {
}
