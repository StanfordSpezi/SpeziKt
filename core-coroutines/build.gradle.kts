plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.core.coroutines"
}

dependencies {
    api(libs.bundles.ktx.coroutines)

    api(project(":core"))

    testImplementation(project(":testing-concurrency"))
}
