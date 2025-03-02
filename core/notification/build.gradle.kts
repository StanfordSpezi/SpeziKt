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
    implementation(libs.kotlinx.serialization.json)

    implementation(project(":core:coroutines"))
    implementation(project(":core:navigation"))
    implementation(project(":modules:account"))
}
