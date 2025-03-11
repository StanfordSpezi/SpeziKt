plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "edu.stanford.spezi.modules.notification"
}

dependencies {
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.functions.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":modules:account"))
    implementation(project(":modules:design"))
    implementation(project(":modules:navigation"))

    implementation(project(":foundation"))
    implementation(project(":core"))
    implementation(project(":core-coroutines"))
    implementation(project(":core-logging"))
    implementation(project(":ui"))
    implementation(project(":storage-credential"))
}
