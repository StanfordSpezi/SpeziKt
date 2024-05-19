plugins {
    alias(libs.plugins.spezikt.library)
    alias(libs.plugins.spezikt.compose)
}

android {
    namespace = "edu.stanford.spezikt.spezi_module.contact"
}

dependencies {
    testImplementation(project(":core:testing"))
    androidTestImplementation(libs.bundles.compose.androidTest)
}