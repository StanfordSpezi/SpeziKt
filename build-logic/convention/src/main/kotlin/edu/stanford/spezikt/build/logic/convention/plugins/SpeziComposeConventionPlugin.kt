package edu.stanford.spezikt.build.logic.convention.plugins

import edu.stanford.spezikt.build.logic.convention.extensions.androidTestImplementation
import edu.stanford.spezikt.build.logic.convention.extensions.commonExtensions
import edu.stanford.spezikt.build.logic.convention.extensions.debugImplementation
import edu.stanford.spezikt.build.logic.convention.extensions.findBundle
import edu.stanford.spezikt.build.logic.convention.extensions.findLibrary
import edu.stanford.spezikt.build.logic.convention.extensions.findVersion
import edu.stanford.spezikt.build.logic.convention.extensions.implementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class SpeziComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        commonExtensions {
            buildFeatures {
                compose = true
            }
            composeOptions {
                kotlinCompilerExtensionVersion = findVersion("kotlinCompilerExtensionVersion")
            }

            dependencies {
                val composeBom = platform(findLibrary("compose-bom"))
                implementation(composeBom)
                implementation(findBundle("compose"))

                implementation(project(":core:design"))

                androidTestImplementation(composeBom)
                androidTestImplementation(findBundle("unit-testing"))
                androidTestImplementation(findBundle("compose-androidTest"))
                debugImplementation(findLibrary("compose-ui-tooling"))
                debugImplementation(findLibrary("compose-ui-test-manifest"))
            }
        }
    }
}