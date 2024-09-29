import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
	val kotlinversion = "1.9.25"
	kotlin("jvm") version kotlinversion
	kotlin("plugin.spring") version kotlinversion
	kotlin("plugin.jpa") version kotlinversion
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}

group = "zinc.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.security:spring-security-oauth2-client")
	implementation("org.springframework.security:spring-security-oauth2-jose")

	implementation("io.ktor:ktor-client-core:2.0.3")
	implementation("io.ktor:ktor-client-cio:2.0.3") // 기본 클라이언트 엔진
	implementation("io.ktor:ktor-client-content-negotiation:2.0.3")
	implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.3")

	runtimeOnly("com.h2database:h2")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.Embeddable")
	annotation("jakarta.persistence.MappedSuperclass")
}

noArg{
	annotation("jakarta.persistence.Entity")
}
