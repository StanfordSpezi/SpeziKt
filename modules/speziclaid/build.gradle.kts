plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.spezi.serialization)
}

android {
    namespace = "edu.stanford.spezi.modules.speziclaid"
}




dependencies {
    api("ch.claid:claid:0.8.8")
    implementation(project(":modules:healthconnectonfhir"))
    implementation(project(":storage-local"))

    api(project(":claid_cough_detection"))

}
