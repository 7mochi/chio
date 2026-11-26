import com.diffplug.spotless.LineEnding
import com.diffplug.spotless.kotlin.KtfmtStep
import java.util.Base64
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask

plugins {
    kotlin("jvm") version "2.4.21"
    id("com.diffplug.spotless") version "8.11.0"
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka") version "2.3.0"
    id("org.jetbrains.dokka-javadoc") version "2.3.0"
    id("com.gradleup.nmcp") version "1.6.2"
    id("com.gradleup.nmcp.aggregation") version "1.6.2"
}

group = "io.github.nanamochi"
version = "0.0.0"

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

dokka {
    dokkaSourceSets.main {
        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl.set(uri("https://github.com/7mochi/chio.jar/blob/master/src/main/kotlin"))
            remoteLineSuffix.set("#L")
        }
    }
}

tasks.jar {
    from("LICENSE") { into("META-INF") }
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    from("LICENSE") { into("META-INF") }
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationJavadoc").flatMap {
        it.outputDirectory
    })
    from("LICENSE") { into("META-INF") }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(sourcesJar)
            artifact(javadocJar)

            pom {
                name = "chio"
                description =
                    "Kotlin library for serializing and deserializing Bancho packets"
                url = "https://github.com/7mochi/chio.jar"

                licenses {
                    license {
                        name = "GNU Affero General Public License v3.0 or later"
                        url = "https://www.gnu.org/licenses/agpl-3.0.txt"
                        distribution = "repo"
                    }
                }

                developers {
                    developer {
                        id = "7mochi"
                        email = "flyingcatdm@gmail.com"
                    }
                }

                scm {
                    url = "https://github.com/7mochi/chio.jar"
                    connection = "scm:git:git://github.com/7mochi/chio.jar.git"
                    developerConnection = "scm:git:ssh://github.com/7mochi/chio.jar.git"
                }
            }
        }
    }
}

signing {
    val signingKey = providers.environmentVariable("GPG_SIGNING_KEY")
    val signingPassphrase = providers.environmentVariable("GPG_SIGNING_PASSPHRASE")

    if (signingKey.isPresent && signingPassphrase.isPresent) {
        val raw = signingKey.get()
        val armored =
            if (raw.contains("BEGIN PGP PRIVATE KEY")) {
                raw
            } else {
                String(Base64.getDecoder().decode(raw), Charsets.UTF_8)
            }

        useInMemoryPgpKeys(armored, signingPassphrase.get())
        sign(publishing.publications)
    }
}

nmcpAggregation {
    centralPortal {
        username = providers.environmentVariable("MAVEN_CENTRAL_USERNAME")
        password = providers.environmentVariable("MAVEN_CENTRAL_PASSWORD")
        publishingType = "USER_MANAGED"
        publicationName = "chio"
    }
}

dependencies {
    nmcpAggregation(project(":"))
}

spotless {
    // Pin LF so Spotless does not search for .gitattributes.
    // The search invalidates the configuration cache
    // https://github.com/gradle/gradle/issues/25469#issuecomment-3444231151
    lineEndings = LineEnding.UNIX

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
