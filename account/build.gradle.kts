plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.account"
}

dependencies {
    api(project(":core"))
    api(project(":foundation"))
    api(project(":ui"))
    api(project(":ui-validation"))
    api(libs.kotlinx.serialization.json)
    implementation(project(":core-coroutines"))
    implementation(project(":core-lifecycle"))
    implementation(project(":storage-local"))
}
