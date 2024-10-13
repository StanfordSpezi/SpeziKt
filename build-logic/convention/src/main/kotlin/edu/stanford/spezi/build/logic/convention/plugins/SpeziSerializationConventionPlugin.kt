package edu.stanford.spezi.build.logic.convention.plugins

import edu.stanford.spezi.build.logic.convention.extensions.apply
import edu.stanford.spezi.build.logic.convention.extensions.findLibrary
import edu.stanford.spezi.build.logic.convention.extensions.implementation
import edu.stanford.spezi.build.logic.convention.model.PluginId
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class SpeziSerializationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        apply(PluginId.SERIALIZATION)

        dependencies {
            implementation(findLibrary("kotlinx-serialization-json"))
        }
    }
}
