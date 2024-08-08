plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.healthconnectonfhir"
}

dependencies {
    api(libs.androidx.health.connect.client)
    api(libs.android.fhir.data.capture)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.gson)
}
