plugins {
    alias(libs.plugins.spezikt.library)
    alias(libs.plugins.spezikt.compose)
}

android {
    namespace = "edu.stanford.spezikt.spezi_module.contact"

    packaging {
        resources {
            excludes += "/META-INF/**.md"
        }
    }
}

dependencies {
    testImplementation(libs.bundles.unit.testing)
    androidTestImplementation(libs.bundles.compose.androidTest)
}