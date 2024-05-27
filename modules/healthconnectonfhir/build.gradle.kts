plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.modules.healthconnectonfhir"
}

dependencies {
    implementation(libs.androidx.health.connect.client)
    implementation(libs.android.fhir.data.capture)
}