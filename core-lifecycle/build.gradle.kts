plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.core.lifecycle"
}

dependencies {
    api(libs.bundles.ktx.coroutines)
    api(project(":core"))
    implementation(libs.androidx.lifecycle.process)
}
