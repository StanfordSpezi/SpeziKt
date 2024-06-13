plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    id("maven-publish")
}

android {
    namespace = "edu.stanford.spezi.modules.healthconnectonfhir"
}

dependencies {
    api(libs.androidx.health.connect.client)
    api(libs.hapi.fhir.structures.r4)
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
