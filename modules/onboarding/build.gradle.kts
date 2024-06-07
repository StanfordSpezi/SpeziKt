plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "edu.stanford.spezi.modules.onboarding"
}

dependencies {
    implementation(libs.accompanist.pager)
    implementation(libs.androidx.foundation)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.functions.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.hilt.navigation.compose)
    implementation(project(":core:coroutines"))
    implementation(project(":core:navigation"))

    testImplementation(libs.bundles.unit.testing)

    androidTestImplementation(libs.bundles.compose.androidTest)
}
