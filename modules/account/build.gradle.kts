plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "edu.stanford.spezi.module.account"

    packaging {
        resources {
            excludes += "/META-INF/**.md"
        }
    }
}

dependencies {
    implementation(libs.firebase.functions.ktx)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore.ktx)
    implementation(project(":core:navigation"))

    testImplementation(libs.bundles.unit.testing)
    androidTestImplementation(libs.bundles.compose.androidTest)
}