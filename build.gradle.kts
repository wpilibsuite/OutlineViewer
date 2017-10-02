
import edu.wpi.first.wpilib.versioning.ReleaseType
import org.gradle.api.Project
import org.gradle.api.plugins.quality.FindBugs
import org.gradle.api.tasks.wrapper.Wrapper
import org.gradle.testing.jacoco.tasks.JacocoReport

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.0")
    }
}
plugins {
    application
    `maven-publish`
    jacoco
    java
    checkstyle
    pmd
    id("edu.wpi.first.wpilib.versioning.WPILibVersioningPlugin") version "2.0"
    id("com.github.johnrengelman.shadow") version "2.0.1"
    id("com.diffplug.gradle.spotless") version "3.5.1"
}

apply {
    plugin("pmd")
    plugin("findbugs")
    plugin("jacoco")
    plugin("org.junit.platform.gradle.plugin")
}

group = "edu.wpi.first.wpilib"

// Spotless is used to lint and reformat source files.
spotless {
    kotlinGradle {
        // Configure the formatting of the Gradle Kotlin DSL files (*.gradle.kts)
        ktlint("0.9.2")
        endWithNewline()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compile(group = "edu.wpi.first.ntcore", name = "ntcore-java", version = "4.+")
    compile(group = "edu.wpi.first.wpiutil", name = "wpiutil-java", version = "3.+")
    compile(group = "org.controlsfx", name = "controlsfx", version = "8.40.14")
    compile(group = "com.google.guava", name = "guava", version = "23.1-jre")

    runtime(group = "edu.wpi.first.ntcore", name = "ntcore-jni", version = "4.+", classifier = "all")

    fun junitJupiter(name: String, version: String = "5.0.1") =
            create(group = "org.junit.jupiter", name = name, version = version)
    fun testFx(name: String, version: String = "4.0.+") =
            create(group = "org.testfx", name = name, version = version)

    testCompile(junitJupiter(name = "junit-jupiter-api"))
    testCompile(junitJupiter(name = "junit-jupiter-engine"))
    testCompile(junitJupiter(name = "junit-jupiter-params"))
    testCompile(testFx(name = "testfx-core", version = "4.0.7-alpha"))
    testCompile(testFx(name = "testfx-junit5", version = "4.0.6-alpha"))
    testCompile(group = "com.google.guava", name = "guava-testlib", version = "23.1-jre")

    testRuntime(testFx(name = "openjfx-monocle", version = "8u76-b04"))
    testRuntime(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.0.1")

}

application {
    mainClassName = "edu.wpi.first.outlineviewer.OutlineViewer"
}

checkstyle {
    configFile = file("$rootDir/checkstyle.xml")
    toolVersion = "8.3"
}

pmd {
    isConsoleOutput = true
    sourceSets = setOf(java.sourceSets["main"], java.sourceSets["test"])
    reportsDir = file("${project.buildDir}/reports/pmd")
    ruleSetFiles = files(file("$rootDir/pmd-ruleset.xml"))
    ruleSets = emptyList()
}

findbugs {
    sourceSets = setOf(java.sourceSets["main"], java.sourceSets["test"])
    effort = "max"
}

tasks.withType<FindBugs> {
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
afterEvaluate {
    val junitPlatformTest : JavaExec by tasks
    jacoco {
        applyTo(junitPlatformTest)
    }
    task<JacocoReport>("jacocoJunit5TestReport") {
        executionData(junitPlatformTest)
        sourceSets(java.sourceSets["main"])
        sourceDirectories = files(java.sourceSets["main"].allSource.srcDirs)
        classDirectories = files(java.sourceSets["main"].output)
    }
}

/*
     * Allows you to run the UI tests in headless mode by calling gradle with the -Pheadless argument
     */
if (project.hasProperty("jenkinsBuild") || project.hasProperty("headless")) {
    println("Running UI Tests Headless")
    junitPlatform {
        filters {
            tags {
                /*
                 * A category for UI tests that cannot run in headless mode, ie work properly with real windows
                 * but not with the virtualized ones in headless mode.
                 */
                exclude("NonHeadlessTests")
            }
        }
    }
    tasks {
        "junitPlatformTest"(JavaExec::class) {
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
            shadow.component(this)
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
    gradleVersion = "4.2.1"
}

/**
 * Retrieves the [findbugs][org.gradle.api.plugins.quality.FindBugsExtension] project extension.
 */
val Project.`findbugs`: org.gradle.api.plugins.quality.FindBugsExtension get() =
    extensions.getByName("findbugs") as org.gradle.api.plugins.quality.FindBugsExtension

/**
 * Configures the [findbugs][org.gradle.api.plugins.quality.FindBugsExtension] project extension.
 */
fun Project.`findbugs`(configure: org.gradle.api.plugins.quality.FindBugsExtension.() -> Unit) =
        extensions.configure("findbugs", configure)

/**
 * Retrieves the [junitPlatform][org.junit.platform.gradle.plugin.JUnitPlatformExtension] project extension.
 */
val Project.`junitPlatform`: org.junit.platform.gradle.plugin.JUnitPlatformExtension get() =
    extensions.getByName("junitPlatform") as org.junit.platform.gradle.plugin.JUnitPlatformExtension

/**
 * Configures the [junitPlatform][org.junit.platform.gradle.plugin.JUnitPlatformExtension] project extension.
 */
fun Project.`junitPlatform`(configure: org.junit.platform.gradle.plugin.JUnitPlatformExtension.() -> Unit) =
        extensions.configure("junitPlatform", configure)
