plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.health"
}

dependencies {
    api(libs.androidx.health.connect.client)
    api(project(":core"))
    api(project(":ui"))
    implementation(project(":core-coroutines"))
    implementation(project(":core-lifecycle"))
    implementation(project(":storage-local"))
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.appcompat)
}
