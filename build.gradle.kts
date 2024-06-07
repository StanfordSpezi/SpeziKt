import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.DokkaTaskPartial

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
    alias(libs.plugins.jetbrains.kotlin.serialization) apply false
}

subprojects {
    setupDokka()
    setupDetekt()
    setupJacoco()
}

installCustomTasks()

tasks.dokkaHtmlMultiModule {
    moduleName.set("Spezi Documentation")
}

fun Project.setupDokka() {
    apply(plugin = rootProject.libs.plugins.dokka.get().pluginId)

    if (this != rootProject) {
        rootProject.tasks.named("dokkaHtmlMultiModule") {
            dependsOn("${project.path}:dokkaHtml")
        }
    }

    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            noAndroidSdkLink.set(false)
            skipDeprecated.set(true)
            skipEmptyPackages.set(true)
            includeNonPublic.set(false)
            jdkVersion.set(JavaVersion.VERSION_17.majorVersion.toInt())
            if (file("README.md").exists()) {
                includes.from("README.md")
            }
        }
    }

    val dokkaHtmlMultiModule = tasks.findByName("dokkaHtmlMultiModule") ?: tasks.create(
        "dokkaHtmlMultiModule",
        DokkaTaskPartial::class.java
    )
    rootProject.tasks.named("dokkaHtmlMultiModule") {
        dependsOn(dokkaHtmlMultiModule)
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
        executionData.setFrom(files("$buildDir/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"))
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
        tasksDir.listFiles { file -> file.extension == "kts" }?.forEach { file -> apply(from = file) }
    }
}
