import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.DokkaExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) version libs.versions.kotlin apply false
    alias(libs.plugins.detekt) version libs.versions.detekt
    alias(libs.plugins.dokka) version libs.versions.dokka
    alias(libs.plugins.google.devtools.ksp) version libs.versions.kspVersion apply false
    alias(libs.plugins.hilt.android) version libs.versions.hiltVersion apply false
    jacoco
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
}

tasks.register("generateDocs") {
    group = "documentation"
    description = "Aggregates documentation for all modules (Dokka v2)."
    dependsOn(subprojects.map { it.path + ":dokkaGeneratePublicationHtml" })
    finalizedBy("copyDocumentationImages")
}

subprojects {
    setupDokka()
    setupDetekt()
    setupJacoco()
}

installCustomTasks()

fun Project.setupDokka() {
    apply(plugin = rootProject.libs.plugins.dokka.get().pluginId)

    extensions.configure<DokkaExtension> {
        moduleName.set(project.name)

        dokkaSourceSets.configureEach {
            skipDeprecated.set(true)
            skipEmptyPackages.set(true)
            jdkVersion.set(JavaVersion.VERSION_17.majorVersion.toInt())

            val readme = file("README.md")
            if (readme.exists()) {
                includes.from(readme)
            }
        }
    }

    if (this != rootProject) {
        rootProject.tasks.named("generateDocs") {
            dependsOn(tasks.named("dokkaGeneratePublicationHtml"))
        }
    }
}

fun Project.setupDetekt() {
    val libs = rootProject.libs
    apply(plugin = libs.plugins.detekt.get().pluginId)
    detekt {
        toolVersion = libs.versions.detekt.get()
        config.setFrom("$rootDir/internal/detekt-config.yml")
        autoCorrect = true
        ignoreFailures = false
        source.setFrom(
            files(
                "src/main",
                "src/test",
                "src/androidTest",
                "build.gradle.kts"
            )
        )
    }

    dependencies {
        detektPlugins(libs.detekt.formatting)
    }

    tasks.withType<Detekt> {
        jvmTarget = JavaVersion.VERSION_17.toString()
        reports {
            xml.required.set(true)
            html.required.set(true)
            txt.required.set(true)
            sarif.required.set(true)
        }
    }
}

fun Project.setupJacoco() {
    apply(plugin = "jacoco")
    val buildDir = layout.buildDirectory.get()
    val coverageExclusions = listOf(
        // Android
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*_Hilt*.class",
        "**/Hilt_*.class",
        "**/*Activity.class",
        "**/*Application.class",
        "**/di/*Module.*",
    )
    val reportTask = tasks.register("jacocoCoverageReport", JacocoReport::class.java) {
        classDirectories.setFrom(
            fileTree("$buildDir/intermediates/classes/debug") {
                exclude(coverageExclusions)
            } + fileTree("$buildDir/tmp/kotlin-classes/debug") {
                exclude(coverageExclusions)
            }
        )
        reports {
            html.required.set(true)
            xml.required.set(true)
        }
        sourceDirectories.setFrom(files("$projectDir/src/main"))

        executionData.setFrom(
            files("$buildDir/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        )
        doLast {
            println("Jacoco report generated in: ${reports.html.outputLocation.get()}")
        }
    }

    tasks.withType<Test>().configureEach {
        configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
        finalizedBy(reportTask)
    }
}

/**
 * Installs all custom tasks defined in /gradle/tasks
 */
fun Project.installCustomTasks() {
    val tasksDir = File("$rootDir/gradle/tasks")
    if (tasksDir.exists() && tasksDir.isDirectory) {
        tasksDir.listFiles { file -> file.extension == "kts" }
            ?.forEach { file -> apply(from = file) }
    }

    tasks.register<Copy>("copyDocumentationImages") {
        group = "documentation"
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        val htmlOutput = layout.buildDirectory.dir("dokka/html")
        fileTree("$rootDir").matching {
            include("**/screens/*.jpg")
        }.forEach { file ->
            val relativePath = file.parentFile.relativeTo(File("$rootDir"))
            from(file.parentFile) {
                include("*.jpg")
            }
            into(htmlOutput.map { it.dir(relativePath.path) })
        }
    }
}
