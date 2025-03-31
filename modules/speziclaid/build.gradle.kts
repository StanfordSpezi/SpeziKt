plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.speziclaid"
}




dependencies {
    api("ch.claid:claid:0.8.8")
    implementation(project(":modules:healthconnectonfhir"))
    implementation(project(":claid_cough_detection"))

}
