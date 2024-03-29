import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.9"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	id("nu.studer.jooq") version "7.1.1"
}

group = "com.banjjoknim"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.liquibase:liquibase-core")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// mysql
	runtimeOnly("com.mysql:mysql-connector-j:8.0.32")
	jooqGenerator("com.mysql:mysql-connector-j")

	// h2
	runtimeOnly("com.h2database:h2")
	jooqGenerator("com.h2database:h2")
	jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1") // java.lang.NoClassDefFoundError: jakarta/xml/bind/annotation/XmlSchema 추가되었음
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jooq {
	version.set("3.16.4")  // default (can be omitted)
	edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)  // default (can be omitted)
	configurations {
		create("main") {  // name of the jOOQ configuration
			generateSchemaSourceOnCompilation.set(false)
//			generateSchemaSourceOnCompilation.set(true)  // default (can be omitted). 기본값 true

			/**
			 * 대부분의 설정은 org.jooq.meta.jaxb 패키지를 참조하도록 한다.
			 * @see nu.studer.gradle.jooq.JooqConfig
			 * @see org.jooq.meta.jaxb.Configuration
			 * @see org.jooq.meta.jaxb.Logging
			 * @see org.jooq.meta.jaxb.Jdbc
			 * @see org.jooq.meta.jaxb.Generator
			 */
			// H2 Code Generation 설정
			jooqConfiguration.apply {
				logging = org.jooq.meta.jaxb.Logging.WARN
				jdbc.apply {
					driver = "org.h2.Driver"
					url = "jdbc:h2:~/test;AUTO_SERVER=TRUE;MODE=MySQL;"
					user = "sa"
					password = ""
				}
				generator.apply {
					name = "org.jooq.codegen.KotlinGenerator"
					database.apply {
						name = "org.jooq.meta.h2.H2Database"
						inputSchema = "PUBLIC" // h2 Database의 Schema 이름은 `PUBLIC` 임.
						isOutputSchemaToDefault = true // A flag to indicate that the outputSchema should be the "default" schema, which generates schema-less, unqualified tables, procedures, etc.
					}
					target.apply {
						packageName = "com.jooq.entity"
						directory = "src/main/kotlin/jooq"  // default (can be omitted)
					}
					generate.apply {
						isDeprecated = false
						isRecords = true
						isImmutablePojos = true
						isFluentSetters = true
						isJavaTimeTypes = true
						isJavaTimeTypes = true
						// Generate POJOs as data classes, when using the KotlinGenerator. Default is true.
						isPojosAsKotlinDataClasses = true
					}
				}

				// MySQL Code Generation 설정
//				jdbc.apply {
//					driver = "com.mysql.cj.jdbc.Driver"
//					url = "jdbc:mysql://127.0.0.1:3306"
//					user = "root"
//					password = ""
//				}
//				generator.apply {
//					name = "org.jooq.codegen.KotlinGenerator"
//					database.apply {
//						name = "org.jooq.meta.mysql.MySQLDatabase"
//						inputSchema = "test"
//						isOutputSchemaToDefault = true
//					}
//					generate.apply {
//						isDeprecated = false
//						isRecords = true
//						isImmutablePojos = true
//						isFluentSetters = true
//						isJavaTimeTypes = true

//                      // Generate POJOs as data classes, when using the KotlinGenerator. Default is true.
//						isPojosAsKotlinDataClasses = true
//					}
//					target.apply {
//						packageName = "com.jooq.entity"
//						directory = "src/main/kotlin/jooq"  // default (can be omitted)
//					}
//					strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
//				}
			}
		}
	}
}
