plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.measurements"
}

dependencies {
    implementation(project(":core:bluetooth"))
    implementation(project(":core:coroutines"))
    api(project(":modules:healthconnectonfhir"))
    implementation(project(":modules:account"))

    implementation(libs.gson)
    implementation(libs.firebase.firestore.ktx)
}
