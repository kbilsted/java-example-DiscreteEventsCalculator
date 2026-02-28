plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
    options.compilerArgs.removeAll(listOf("--enable-preview"))
}

tasks.withType<Test>().configureEach {
    jvmArgs = (jvmArgs ?: emptyList()).filter { it != "--enable-preview" }
}

sourceSets {
    main {
        java.srcDir("src/generated/java")
    }
    test {
        java.srcDir("src/generatedTest/java")
    }
}
