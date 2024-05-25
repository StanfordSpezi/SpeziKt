plugins {
    alias(libs.plugins.spezi.library)

}

android {
    namespace = "edu.stanford.spezi.core.navigation"
}

dependencies {
    implementation(libs.androidx.core.ktx)
}