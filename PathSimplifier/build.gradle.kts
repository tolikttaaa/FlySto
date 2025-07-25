plugins {
    kotlin("jvm")
    application
}

group = "com.ttaaa.flysto"

repositories {
    mavenCentral()
}

dependencies {
    //Picocli
    implementation("info.picocli:picocli:4.7.5")
    annotationProcessor("info.picocli:picocli-codegen:4.7.5")

    // SLF4J API
    implementation("org.slf4j:slf4j-api:2.0.9")

    // Log4j 2 binding for SLF4J
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")

    // Apache Common CSV
    implementation("org.apache.commons:commons-csv:1.10.0")

    // For JUnit 5
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    // MockK
    testImplementation("io.mockk:mockk:1.13.10")
    // For AssertJ, for fluent and readable assertions
    testImplementation("org.assertj:assertj-core:3.25.3")
}

application {
    mainClass.set("com.ttaaa.flysto.pathsimplifier.PathSimplifierAppKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.ttaaa.flysto.pathsimplifier.PathSimplifierAppKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
kotlin {
    jvmToolchain(21)
}
