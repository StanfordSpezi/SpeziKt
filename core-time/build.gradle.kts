plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.core.time"
}

dependencies {
    api(project(":core"))
}
