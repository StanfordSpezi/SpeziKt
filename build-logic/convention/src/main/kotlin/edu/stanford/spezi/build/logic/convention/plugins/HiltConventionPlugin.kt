package edu.stanford.spezi.build.logic.convention.plugins

import dagger.hilt.android.plugin.HiltExtension
import edu.stanford.spezi.build.logic.convention.extensions.androidTestImplementation
import edu.stanford.spezi.build.logic.convention.extensions.apply
import edu.stanford.spezi.build.logic.convention.extensions.extension
import edu.stanford.spezi.build.logic.convention.extensions.findLibrary
import edu.stanford.spezi.build.logic.convention.extensions.implementation
import edu.stanford.spezi.build.logic.convention.model.PluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        apply(PluginId.HILT)
        apply(PluginId.KSP)

        dependencies {
            implementation(findLibrary("hilt-core"))
            add("ksp", findLibrary("hilt-compiler"))

            androidTestImplementation(findLibrary("hilt-test"))
            add("kspAndroidTest", findLibrary("hilt-test-compiler"))
        }

        extension<HiltExtension> {
            enableAggregatingTask = true
        }
    }
}