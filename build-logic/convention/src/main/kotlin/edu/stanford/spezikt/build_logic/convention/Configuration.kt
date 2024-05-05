package edu.stanford.spezikt.build_logic.convention

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


/**
 * Configures the SpeziKt Android Library Compose plugin for the project.
 */
internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        compileSdk = libs.findVersion("compileSdk").get().toString().toInt()

        buildFeatures {
            compose = true
        }

        composeOptions {
            kotlinCompilerExtensionVersion =
                libs.findVersion("kotlinCompilerExtensionVersion").get().toString()
        }

        defaultConfig {
            minSdk = libs.findVersion("minSdk").get().toString().toInt()
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        dependencies {
            val bom = libs.findLibrary("androidx-compose-bom").get()
            add("implementation", platform(bom))
            add("androidTestImplementation", platform(bom))

            add("implementation", project(":core:designsystem"))
            add("implementation", libs.findBundle("compose-implementation").get())

            add("testImplementation", libs.findBundle("compose-testImplementation").get())

            add("androidTestImplementation", libs.findLibrary("androidx.junit").get())
            add("androidTestImplementation", libs.findLibrary("androidx.espresso.core").get())
            add("androidTestImplementation", libs.findLibrary("androidx.ui.test.junit4").get())
        }
    }
    configureKotlin()
}

/**
 * Configures the Kotlin compiler options for the project.
 * The compiler options are set to use Java 17 as the target JVM version.
 * Additionally, the compiler options are set to treat all warnings as errors.
 */
internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()
            freeCompilerArgs =
                freeCompilerArgs + listOf("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}

/**
 * The purpose of this function is to optimize the build process.
 * If there are no Android tests for a variant, there's no need to spend time
 * and resources to build and run these non-existent tests. By disabling the tests for these variants,
 * the build process can be faster and more efficient.
 */
internal fun LibraryAndroidComponentsExtension.disableUnnecessaryAndroidTests(
    project: Project,
) = beforeVariants {
    @Suppress("UnstableApiUsage")
    it.androidTest.enable = it.androidTest.enable
            && project.projectDir.resolve("src/androidTest").exists()
}