package edu.stanford.spezi.build.logic.convention.plugins

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import edu.stanford.spezi.build.logic.convention.extensions.android
import edu.stanford.spezi.build.logic.convention.extensions.findVersion
import edu.stanford.spezi.build.logic.convention.extensions.isLibrary
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class SpeziBaseConfigConventionPlugin : Plugin<Project> {
    private val java = JavaVersion.VERSION_17

    override fun apply(target: Project) = with(target) {
        android {
            compileSdk = findVersion("compileSdk").toInt()

            defaultConfig {
                minSdk = findVersion("minSdk").toInt()
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            compileOptions {
                sourceCompatibility = java
                targetCompatibility = java
            }

            packaging {
                resources {
                    excludes += "/META-INF/**.md"
                }
            }
        }

        tasks.withType<KotlinCompilationTask<*>>().configureEach {
            compilerOptions {
                languageVersion.set(KotlinVersion.KOTLIN_2_0)
                freeCompilerArgs.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
            }
        }

        tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                jvmTarget = java.toString()
            }
        }

        /**
         * The purpose of this function is to optimize the build process.
         * If there are no Android tests for a variant, there's no need to spend time
         * and resources to build and run these non-existent tests. By disabling the tests for these variants,
         * the build process can be faster and more efficient.
         */
        if (isLibrary()) {
            extensions.configure<LibraryAndroidComponentsExtension> {
                beforeVariants {
                    it.androidTest.enable = it.androidTest.enable && projectDir.resolve("src/androidTest").exists()
                }
            }
        }
    }
}
