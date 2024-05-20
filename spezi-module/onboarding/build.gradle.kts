plugins {
    alias(libs.plugins.spezikt.library)
    alias(libs.plugins.spezikt.compose)
    alias(libs.plugins.spezikt.hilt)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "edu.stanford.spezikt.spezi_module.onboarding"
}

dependencies {
    implementation(libs.firebase.functions.ktx)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.firebase.auth)

    implementation(project(":core:coroutines"))

    testImplementation(libs.bundles.unit.testing)

    androidTestImplementation(libs.bundles.compose.androidTest)
}