import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.time.Instant
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.spotbugs.SpotBugsTask
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import groovy.lang.GroovyObject

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
    id("edu.wpi.first.wpilib.versioning.WPILibVersioningPlugin") version "4.0.1"
    id("edu.wpi.first.wpilib.repositories.WPILibRepositoriesPlugin") version "2020.1"
    id("com.jfrog.artifactory") version "4.9.8"
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("com.diffplug.gradle.spotless") version "3.23.0"
    id("com.github.spotbugs") version "1.7.1"
}

if (hasProperty("buildServer")) {
    wpilibVersioning.setBuildServerMode(true)
}

if (hasProperty("releaseMode")) {
    wpilibVersioning.setReleaseMode(true)
}

repositories {
    mavenCentral()
}
if (hasProperty("releaseMode")) {
    wpilibRepositories.addAllReleaseRepositories(project)
} else {
    wpilibRepositories.addAllDevelopmentRepositories(project)
}

wpilibVersioning.getVersion().finalizeValue()
version = wpilibVersioning.getVersion().get()

if (System.getenv()["RUN_AZURE_ARTIFACTORY_RELEASE"] != null) {
    artifactory {
        setContextUrl("https://frcmaven.wpi.edu/artifactory") // base artifactory url
        publish(delegateClosureOf<PublisherConfig> {
            repository(delegateClosureOf<GroovyObject> {
                if (project.hasProperty("releaseRepoPublish")) {
                    setProperty("repoKey", "release")
                } else {
                    setProperty("repoKey", "development")
                }
                setProperty("username", System.getenv()["ARTIFACTORY_PUBLISH_USERNAME"])
                setProperty("password", System.getenv()["ARTIFACTORY_PUBLISH_PASSWORD"])
                setProperty("maven", true)
            })
            defaults(delegateClosureOf<GroovyObject> {
                invokeMethod("publications", "app")
            })
        })
    }

    tasks.named("publish") {
        dependsOn(tasks.named("artifactoryPublish"))
    }
}

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
    toolVersion = "8.20"
}

pmd {
    toolVersion = "6.14.0"
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

jacoco {
    toolVersion = "0.8.4"
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

tasks.withType<Wrapper>().configureEach {
    gradleVersion = "5.4.1"
}
