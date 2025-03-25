plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.speziclaid"
}

dependencies {
    implementation("ch.claid:claid:0.6.4")
}
