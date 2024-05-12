plugins {
    alias(libs.plugins.spezikt.android.library.compose)
}

android {
    namespace = "edu.stanford.spezikt.spezi_module.contact"

    packagingOptions {
        exclude("META-INF/**.md")
    }
}

dependencies {
    androidTestImplementation(libs.bundles.mockk.androidTestImplementation)
    testImplementation(libs.truth)
}