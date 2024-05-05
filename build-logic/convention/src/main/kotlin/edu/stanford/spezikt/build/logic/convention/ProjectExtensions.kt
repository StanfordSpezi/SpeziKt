package edu.stanford.spezikt.build.logic.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.findBundle(name: String) = libs.findBundle(name).get()

internal fun Project.findLibrary(name: String) = libs.findLibrary(name).get()

internal fun Project.findVersion(name: String) = libs.findVersion(name).get().toString()

internal fun Project.findPlugin(name: String) = libs.findPlugin(name).get().toString()