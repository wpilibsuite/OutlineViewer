import edu.wpi.first.wpilib.versioning.ReleaseType
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.time.Instant
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.spotbugs.SpotBugsTask

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
}
plugins {
    `maven-publish`
    jacoco
    java
    checkstyle
    application
    pmd
    id("edu.wpi.first.wpilib.versioning.WPILibVersioningPlugin") version "2.3"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("com.diffplug.gradle.spotless") version "3.23.0"
    id("com.github.spotbugs") version "1.7.1"
}

// Ensure that the WPILibVersioningPlugin is setup by setting the release type, if releaseType wasn't
// already specified on the command line
if (!hasProperty("releaseType")) {
    WPILibVersion {
        releaseType = ReleaseType.DEV
    }
}

version = getWPILibVersion()

val theMainClassName = "edu.wpi.first.outlineviewer.Main"

tasks.withType<Jar>().configureEach {
    manifest {
        attributes["Implementation-Version"] = project.version as String
        attributes["Built-Date"] = Instant.now().toString()
        attributes["Main-Class"] = theMainClassName
    }
}

application {
    mainClassName = theMainClassName
}

repositories {
    mavenCentral()
}

// Spotless is used to lint and reformat source files.
spotless {
    kotlinGradle {
        ktlint("0.32.0")
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

createNativeConfigurations()

dependencies {
    // JavaFX dependencies
    javafx("base")
    javafx("controls")
    javafx("fxml")
    javafx("graphics")

    val ntcoreVersion = "2019.+"
    val wpiUtilVersion = "2019.+"

    compile(group = "edu.wpi.first.ntcore", name = "ntcore-java", version = ntcoreVersion)
    native(group = "edu.wpi.first.ntcore", name = "ntcore-jni", version = ntcoreVersion, classifierFunction = ::wpilibClassifier)
    compile(group = "edu.wpi.first.wpiutil", name = "wpiutil-java", version = wpiUtilVersion)

    compile(group = "com.google.guava", name = "guava", version = "27.1-jre")
    compile(group = "org.controlsfx", name = "controlsfx", version = "11.0.0")

    fun junitJupiter(name: String, version: String = "5.4.2") =
        create(group = "org.junit.jupiter", name = name, version = version)
    fun testFx(name: String, version: String = "4.0.15-alpha") =
        create(group = "org.testfx", name = name, version = version)

    testImplementation(junitJupiter(name = "junit-jupiter-api"))
    testImplementation(junitJupiter(name = "junit-jupiter-engine"))
    testImplementation(junitJupiter(name = "junit-jupiter-params"))
    testImplementation(group = "com.google.guava", name = "guava-testlib", version = "27.1-jre")
    testImplementation(testFx(name = "testfx-core"))
    testImplementation(testFx(name = "testfx-junit5"))

    testRuntime(testFx(name = "openjfx-monocle", version = "jdk-11+26"))
    testRuntime(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.4.2")
}

checkstyle {
    toolVersion = "8.12"
}

pmd {
    toolVersion = "6.7.0"
    isConsoleOutput = true
    sourceSets = setOf(project.sourceSets["main"], project.sourceSets["test"])
    reportsDir = file("${project.buildDir}/reports/pmd")
    ruleSetFiles = files(file("$rootDir/pmd-ruleset.xml"))
    ruleSets = emptyList()
}

tasks.withType<JavaCompile>().configureEach {
    // UTF-8 characters are used in menus
    options.encoding = "UTF-8"
}

tasks.withType<SpotBugsTask>() {
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

jacoco {
    toolVersion = "0.8.2"
}

tasks.withType<JacocoReport>().configureEach {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

tasks.withType<Test>().configureEach {
    // TODO: re-enable when TestFX (or the underlying JavaFX problem) is fixed
    println("UI tests will not be run due to TestFX being broken when headless on Java 10.")
    println("See: https://github.com/javafxports/openjdk-jfx/issues/66")
    // Link: https://github.com/javafxports/openjdk-jfx/issues/66
    useJUnitPlatform {
        excludeTags("UI")
    }
}

tasks.withType<Javadoc>().configureEach {
    isFailOnError = false
}

val nativeShadowTasks = NativePlatforms.values().map { platform ->
    tasks.create<ShadowJar>("shadowJar-${platform.platformName}") {
        classifier = platform.platformName
        configurations = listOf(
                project.configurations.getByName("compile"),
                project.configurations.getByName(platform.platformName)
        )
        from(
                project.sourceSets["main"].output
        )
    }
}

tasks.create("shadowJarAllPlatforms") {
    nativeShadowTasks.forEach {
        this.dependsOn(it)
    }
}

tasks.withType<ShadowJar>().configureEach {
    exclude("module-info.class")
}

publishing {
    publications {
        create<MavenPublication>("app") {
            groupId = "edu.wpi.first.wpilib"
            artifactId = "OutlineViewer"
            version = project.version as String
            nativeShadowTasks.forEach {
                artifact(it) {
                    classifier = it.classifier
                }
            }
        }
    }
}

/**
 * @return publishVersion property if exists, otherwise
 * [edu.wpi.first.wpilib.versioning.WPILibVersioningPluginExtension.version] value or fallback
 * if that value is the empty string.
 */
fun getWPILibVersion(fallback: String = "0.0.0"): String {
    if (project.hasProperty("publishVersion")) {
        val publishVersion: String by project
        return publishVersion
    } else if (WPILibVersion.version != "") {
        return WPILibVersion.version
    } else {
        return fallback
    }
}

tasks.withType<Wrapper>().configureEach {
    gradleVersion = "5.0"
}
