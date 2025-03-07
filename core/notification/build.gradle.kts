plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "edu.stanford.spezi.core.notification"
}

dependencies {
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.functions.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":core:coroutines"))
    implementation(project(":core:design"))
    implementation(project(":core:navigation"))
    implementation(project(":modules:account"))
    implementation(project(":modules:storage"))

    implementation(project(":spezi:foundation"))
    implementation(project(":spezi:core"))
    implementation(project(":spezi:ui"))
    implementation(project(":spezi:credentialstorage"))
}
