plugins {
    alias(libs.plugins.spezi.library)
    id("maven-publish")
}

android {
    namespace = "edu.stanford.spezi.modules.healthconnectonfhir"
}

dependencies {
    implementation(libs.androidx.health.connect.client)
    implementation(libs.android.fhir.data.capture)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "edu.stanford.spezi.modules"
                artifactId = "healthconnectonfhir"
                version = "0.0.1"
            }
        }
    }
}
