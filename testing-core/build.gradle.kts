plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.testing.core"
}

dependencies {
    api(project(":core"))
    api(project(":core-viewmodel"))
    api(libs.bundles.unit.testing)
}
