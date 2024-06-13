plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.spezi.base)
}

android {
    namespace = "edu.stanford.spezi.core.testing"
}

dependencies {
    implementation(project(":core:coroutines"))

    api(libs.bundles.unit.testing)
}
