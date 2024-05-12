package edu.stanford.spezikt.build.logic.convention

import org.gradle.api.artifacts.dsl.DependencyHandler

internal fun DependencyHandler.implementation(dependency: Any) {
    add("implementation", dependency)
}

internal fun DependencyHandler.testImplementation(dependency: Any) {
    add("testImplementation", dependency)
}

internal fun DependencyHandler.androidTestImplementation(dependency: Any) {
    add("androidTestImplementation", dependency)
}