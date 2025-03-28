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
    {
        exclude(group = "com.google.firebase", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")    }
    implementation(libs.firebase.functions.ktx)
    {
        exclude(group = "com.google.firebase", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")    }
    implementation(libs.firebase.messaging.ktx)
    {
        exclude(group = "com.google.firebase", module = "protolite-well-known-types")
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")    }
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
