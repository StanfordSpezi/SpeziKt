plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
}

android {
    namespace = "edu.stanford.spezi.ui.personalinfo"
}

dependencies {
    implementation(project(":core-logging"))

    api(project(":ui"))
    implementation(project(":ui-testing"))
}
