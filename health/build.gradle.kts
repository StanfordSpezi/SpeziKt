plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.health"
}

dependencies {
    api(libs.androidx.health.connect.client)
    api(project(":core"))
    api(project(":core-logging"))
    implementation(project(":core-coroutines"))
    implementation(project(":storage-local"))
    implementation(libs.androidx.fragment.ktx)
}
