plugins {
    alias(libs.plugins.spezi.application)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.healthconnectonfhir"
}

dependencies {
    implementation("androidx.health.connect:connect-client:1.1.0-alpha02")
    implementation("com.google.android.fhir:data-capture:1.0.0")

    implementation(project(":core:utils"))
    implementation(project(":core:coroutines"))
}