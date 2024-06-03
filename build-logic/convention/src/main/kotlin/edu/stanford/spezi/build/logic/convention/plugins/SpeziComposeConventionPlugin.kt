package edu.stanford.spezi.build.logic.convention.plugins

import edu.stanford.spezi.build.logic.convention.extensions.androidTestImplementation
import edu.stanford.spezi.build.logic.convention.extensions.apply
import edu.stanford.spezi.build.logic.convention.extensions.commonExtensions
import edu.stanford.spezi.build.logic.convention.extensions.debugImplementation
import edu.stanford.spezi.build.logic.convention.extensions.findBundle
import edu.stanford.spezi.build.logic.convention.extensions.findLibrary
import edu.stanford.spezi.build.logic.convention.extensions.implementation
import edu.stanford.spezi.build.logic.convention.model.PluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class SpeziComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        apply(PluginId.COMPOSE_COMPILER)

        commonExtensions {
            buildFeatures {
                compose = true
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
