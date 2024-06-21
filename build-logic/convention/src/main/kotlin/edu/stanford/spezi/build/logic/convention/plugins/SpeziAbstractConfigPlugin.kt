package edu.stanford.spezi.build.logic.convention.plugins

import edu.stanford.spezi.build.logic.convention.extensions.android
import edu.stanford.spezi.build.logic.convention.extensions.androidTestImplementation
import edu.stanford.spezi.build.logic.convention.extensions.apply
import edu.stanford.spezi.build.logic.convention.extensions.implementation
import edu.stanford.spezi.build.logic.convention.extensions.testImplementation
import edu.stanford.spezi.build.logic.convention.model.PluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.provideDelegate

abstract class SpeziAbstractConfigPlugin(private val modulePlugin: PluginId) : Plugin<Project> {
    private val defaultConfig by lazy { SpeziBaseConfigConventionPlugin() }

    override fun apply(project: Project) = with(project) {
        apply(modulePlugin)
        apply(PluginId.JETBRAINS_KOTLIN_ANDROID)

        defaultConfig.apply(this)

        android {
            defaultConfig {
                testInstrumentationRunner = "edu.stanford.spezi.core.testing.HiltApplicationTestRunner"
            }
        }

        dependencies {
            implementation(project(":core:utils"))
            implementation(project(":core:logging"))

            testImplementation(project(":core:testing"))

            androidTestImplementation(project(":core:testing"))
        }
    }
}
