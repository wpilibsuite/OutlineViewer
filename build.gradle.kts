import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.time.Instant
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
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
    id("edu.wpi.first.wpilib.versioning.WPILibVersioningPlugin") version "4.0.2"
    id("edu.wpi.first.wpilib.repositories.WPILibRepositoriesPlugin") version "2020.2"
    id("com.jfrog.artifactory") version "4.15.1"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("com.diffplug.gradle.spotless") version "3.28.0"
    id("com.github.spotbugs") version "4.0.4"
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
                if (project.hasProperty("releaseMode")) {
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
        clientConfig.info.setBuildName("OutlineViewer")
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
        ktlint("0.36.0")
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

    val ntcoreVersion = "2020.+"
    val wpiUtilVersion = "2020.+"

    implementation(group = "edu.wpi.first.ntcore", name = "ntcore-java", version = ntcoreVersion)
    native(group = "edu.wpi.first.ntcore", name = "ntcore-jni", version = ntcoreVersion, classifierFunction = ::wpilibClassifier)
    implementation(group = "edu.wpi.first.wpiutil", name = "wpiutil-java", version = wpiUtilVersion)

    implementation(group = "com.google.guava", name = "guava", version = "27.1-jre")
    implementation(group = "org.controlsfx", name = "controlsfx", version = "11.0.0")

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

    testRuntimeOnly(testFx(name = "openjfx-monocle", version = "jdk-11+26"))
    testRuntimeOnly(group = "org.junit.platform", name = "junit-platform-launcher", version = "1.4.2")
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

spotbugs {
    ignoreFailures.set(false)
    showProgress.set(true)
    effort.set(com.github.spotbugs.snom.Effort.MAX)

    tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
        reports {
            create("text") {
                isEnabled = true
            }
        }
        finalizedBy(task("${name}Report") {
            mustRunAfter(this@withType)
            doLast {
                this@withType
                        .reports.first()
                        .destination
                        .takeIf { it.exists() }
                        ?.readText()
                        .takeIf { !it.isNullOrBlank() }
                        ?.also { logger.warn(it) }
            }
        })
    }
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
    useJUnitPlatform {
        if (project.hasProperty("skipUI")) {
            excludeTags("UI")
        }
        if (!project.hasProperty("visibleUiTests")) {
            jvmArgs = listOf(
                    "-Djava.awt.headless=true",
                    "-Dtestfx.robot=glass",
                    "-Dtestfx.headless=true",
                    "-Dprism.order=sw",
                    "-Dprism.text=t2k"
            )
            excludeTags("NonHeadlessTests")
        }
        systemProperty("junit.jupiter.extensions.autodetection.enabled", true)
    }
    finalizedBy("jacocoTestReport")
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
    gradleVersion = "6.2.2"
}
