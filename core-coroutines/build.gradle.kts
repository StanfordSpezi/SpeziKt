plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.core.coroutines"
}

dependencies {
    api(libs.bundles.ktx.coroutines)

    api(project(":core"))
}
