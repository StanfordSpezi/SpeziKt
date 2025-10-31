package edu.stanford.spezi.build.logic.convention.plugins

import edu.stanford.spezi.build.logic.convention.extensions.android
import edu.stanford.spezi.build.logic.convention.extensions.findLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class DesugaringConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        android {
            compileOptions {
                isCoreLibraryDesugaringEnabled = true
            }
        }

        dependencies {
            add("coreLibraryDesugaring", findLibrary("android-desugaring"))
        }
    }
}
