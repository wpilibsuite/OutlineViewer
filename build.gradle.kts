
import com.github.spotbugs.SpotBugsTask
import edu.wpi.first.wpilib.versioning.ReleaseType
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.testing.jacoco.tasks.JacocoReport
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    `maven-publish`
    jacoco
    java
    checkstyle
    //pmd
    id("edu.wpi.first.wpilib.versioning.WPILibVersioningPlugin") version "2.0"
    id("com.github.johnrengelman.shadow") version "2.0.4"
    id("com.diffplug.gradle.spotless") version "3.10.0"
    id("com.github.spotbugs") version "1.6.1"
}

apply {
    plugin("pmd")
    plugin("com.github.spotbugs")
    plugin("jacoco")
}

group = "edu.wpi.first.wpilib"

// Spotless is used to lint and reformat source files.
spotless {
    kotlinGradle {
        // Configure the formatting of the Gradle Kotlin DSL files (*.gradle.kts)
        ktlint()
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
    format("extraneous") {
        target("src/**/*.fxml", "src/**/*.css", "*.xml", "*.yml", "*.md")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "edu.wpi.first.ntcore", name = "ntcore-java", version = "4.+")
    implementation(group = "edu.wpi.first.wpiutil", name = "wpiutil-java", version = "3.+")
    implementation(group = "org.controlsfx", name = "controlsfx", version = "9.0.0")
    implementation(group = "com.google.guava", name = "guava", version = "24.1.1-jre")

    runtime(group = "edu.wpi.first.ntcore", name = "ntcore-jni", version = "4.+", classifier = "all")

    fun junitJupiter(name: String, version: String = "5.1.1") =
            create(group = "org.junit.jupiter", name = name, version = version)
    fun testFx(name: String, version: String = "4.0.+") =
            create(group = "org.testfx", name = name, version = version)

    testImplementation(junitJupiter(name = "junit-jupiter-api"))
    testImplementation(junitJupiter(name = "junit-jupiter-engine"))
    testImplementation(junitJupiter(name = "junit-jupiter-params"))
    testImplementation(testFx(name = "testfx-core"))
    testImplementation(testFx(name = "testfx-junit5"))
    testImplementation(group = "com.google.guava", name = "guava-testlib", version = "24.1.1-jre")

    testRuntime(testFx(name = "openjfx-monocle", version = "jdk-9+181"))
}

application {
    mainClassName = "edu.wpi.first.outlineviewer.OutlineViewer"
}

checkstyle {
    configFile = file("$rootDir/checkstyle.xml")
}

//pmd {
//    toolVersion = "6.2.0"
//    isConsoleOutput = true
//    sourceSets = setOf(java.sourceSets["main"], java.sourceSets["test"])
//    reportsDir = file("${project.buildDir}/reports/pmd")
//    ruleSetFiles = files(file("$rootDir/pmd-ruleset.xml"))
//    ruleSets = emptyList()
//}

spotbugs {
    sourceSets = setOf(java.sourceSets["main"], java.sourceSets["test"])
    toolVersion = "3.1.3"
    effort = "max"
}

tasks.withType<SpotBugsTask> {
    reports {
        xml.isEnabled = false
        emacs.isEnabled = true
    }
    finalizedBy(task("${name}Report") {
        mustRunAfter(this@withType)
        doLast {
            this@withType
                    .reports
                    .emacs
                    .destination
                    .takeIf { it.exists() }
                    ?.readText()
                    .takeIf { !it.isNullOrBlank() }
                    ?.also { logger.warn(it) }
        }
    })
}

tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

/*
 * Run UI tests in headless mode on Jenkins or when the `visibleUiTests` property is not set.
 */
if (project.hasProperty("jenkinsBuild") || !project.hasProperty("visibleUiTests")) {
    tasks.withType<Test> {
        useJUnitPlatform {
            exclude("NonHeadlessTests")
            jvmArgs = listOf(
                    "-Djava.awt.headless=true",
                    "-Dtestfx.robot=glass",
                    "-Dtestfx.headless=true",
                    "-Dprism.order=sw",
                    "-Dprism.text=t2k"
            )
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("OutlineViewer") {
            artifactId = "OutlineViewer"
            getWPILibVersion()?.let { version = it }
            val shadowJar: ShadowJar by tasks
            artifact (shadowJar) {
                classifier = null
            }
        }
    }
}

// Ensure that the WPILibVersioningPlugin is setup by setting the release type, if releaseType wasn't
// already specified on the command line
if (!hasProperty("releaseType")) {
    WPILibVersion {
        releaseType = ReleaseType.DEV
    }
}

/**
 * @return [edu.wpi.first.wpilib.versioning.WPILibVersioningPluginExtension.version] value or null
 * if that value is the empty string.
 */
fun getWPILibVersion(): String? = if (WPILibVersion.version != "") WPILibVersion.version else null

task<Wrapper>("wrapper") {
    gradleVersion = "4.7"
}
