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
    implementation(project(":core:coroutines"))
    implementation(project(":core:navigation"))
    implementation(project(":core:utils"))

    implementation(libs.hilt.navigation.compose)

    implementation(libs.firebase.functions.ktx)

    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore.ktx)

    testImplementation(libs.bundles.unit.testing)
    androidTestImplementation(libs.bundles.compose.androidTest)
}
