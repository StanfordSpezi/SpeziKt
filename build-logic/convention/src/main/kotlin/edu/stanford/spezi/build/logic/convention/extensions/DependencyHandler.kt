package edu.stanford.spezi.build.logic.convention.extensions

import org.gradle.api.artifacts.dsl.DependencyHandler

internal fun DependencyHandler.implementation(dependency: Any) {
    add("implementation", dependency)
}

internal fun DependencyHandler.testImplementation(dependency: Any) {
    add("testImplementation", dependency)
}

internal fun DependencyHandler.debugImplementation(dependency: Any) {
    add("debugImplementation", dependency)
}

internal fun DependencyHandler.androidTestImplementation(dependency: Any) {
    add("androidTestImplementation", dependency)
}
