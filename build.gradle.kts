import com.diffplug.spotless.kotlin.KtfmtStep

plugins {
    kotlin("jvm") version "2.4.21"
    id("com.diffplug.spotless") version "8.11.0"
}

group = "io.github.nanamochi"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(25)
    compilerOptions {
        // Java cannot call the unboxed methods generated for value classes.
        // This flag adds boxed constructors and getters for ButtonState, Mods and BinaryReader.
        freeCompilerArgs.add("-Xjvm-expose-boxed")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlinx:kotlinx-io-core:0.9.2")

    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.3")
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    format("misc") {
        target(".gitignore", ".gitattributes", "*.md")
        leadingTabsToSpaces(4)
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlin {
        target("**/*.kt")
        targetExclude("build/**", ".gradle/**", ".idea/**")
        ktfmt("0.64").kotlinlangStyle().configure {
            it.setMaxWidth(100)
            it.setRemoveUnusedImports(true)
            it.setTrailingCommaManagementStrategy(KtfmtStep.TrailingCommaManagementStrategy.COMPLETE)
        }
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }
}
