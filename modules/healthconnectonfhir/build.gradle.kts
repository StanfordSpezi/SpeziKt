plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
<<<<<<< HEAD
    id("maven-publish")
=======
>>>>>>> b9f4ae6afe83983c2f4a7c5ddbf57c10d7e39ca1
}

android {
    namespace = "edu.stanford.spezi.modules.healthconnectonfhir"
}

dependencies {
    api(libs.androidx.health.connect.client)
    api(libs.hapi.fhir.structures.r4)
}
<<<<<<< HEAD

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
=======
>>>>>>> b9f4ae6afe83983c2f4a7c5ddbf57c10d7e39ca1
