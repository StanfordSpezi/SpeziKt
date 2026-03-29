plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.testing.concurrency"
}

dependencies {
    api(libs.coroutines.test)
    api(libs.bundles.unit.testing)
}
