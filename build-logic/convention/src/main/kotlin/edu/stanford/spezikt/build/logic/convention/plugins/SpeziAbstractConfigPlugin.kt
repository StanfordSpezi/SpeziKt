package edu.stanford.spezikt.build.logic.convention.plugins

import edu.stanford.spezikt.build.logic.convention.extensions.apply
import edu.stanford.spezikt.build.logic.convention.extensions.findBundle
import edu.stanford.spezikt.build.logic.convention.extensions.implementation
import edu.stanford.spezikt.build.logic.convention.extensions.testImplementation
import edu.stanford.spezikt.build.logic.convention.model.PluginId
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

        dependencies {
            implementation(project(":core:logging"))
            testImplementation(findBundle("unit-testing"))
        }
    }
}