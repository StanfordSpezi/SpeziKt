plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.core"
}

dependencies {
    implementation(project(":core-logging"))
    api(project(":foundation"))
}
