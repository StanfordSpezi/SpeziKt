package edu.stanford.spezi.build.logic.convention.extensions

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import edu.stanford.spezi.build.logic.convention.model.PluginId
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

private val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.findBundle(name: String) = libs.findBundle(name).get()

internal fun Project.findLibrary(name: String) = libs.findLibrary(name).get()

internal fun Project.findVersion(name: String) = libs.findVersion(name).get().toString()

internal fun Project.apply(pluginId: PluginId) = plugins.apply(pluginId.id)

internal fun Project.isApp() = plugins.hasPlugin(PluginId.ANDROID_APPLICATION.id)

internal fun Project.isLibrary() = plugins.hasPlugin(PluginId.ANDROID_LIBRARY.id)

internal fun Project.hasAndroidTest() = projectDir.resolve("src/androidTest").exists()

inline fun <reified T : Any> Project.extension(configBlock: T.() -> Unit) {
    extensions.getByType<T>().apply(configBlock)
}

internal fun Project.android(configBlock: CommonExtension<*, *, *, *, *, *>.() -> Unit) {
    when {
        isApp() -> extension<BaseAppModuleExtension>(configBlock)
        isLibrary() -> extension<LibraryExtension>(configBlock)
        else -> error("commonExtensions was called before setting the module type plugin")
    }
}
