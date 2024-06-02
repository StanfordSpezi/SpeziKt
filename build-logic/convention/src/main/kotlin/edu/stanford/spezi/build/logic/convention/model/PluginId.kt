package edu.stanford.spezi.build.logic.convention.model

enum class PluginId(val id: String) {
    ANDROID_APPLICATION(id = "com.android.application"),
    ANDROID_LIBRARY(id = "com.android.library"),
    JETBRAINS_KOTLIN_ANDROID(id = "org.jetbrains.kotlin.android"),
    HILT(id = "com.google.dagger.hilt.android"),
    KSP(id = "com.google.devtools.ksp"),
    COMPOSE_COMPILER("org.jetbrains.kotlin.plugin.compose")
}
