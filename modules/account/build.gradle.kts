plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.compose)
    alias(libs.plugins.spezi.hilt)
}

android {
    namespace = "edu.stanford.spezi.modules.account"

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/**.md"
        }
    }
}

dependencies {
    implementation(project(":modules:design"))
    implementation(project(":modules:navigation"))
    implementation(project(":modules:utils"))

    implementation(project(":foundation"))
    implementation(project(":core"))
    implementation(project(":core-coroutines"))
    implementation(project(":core-logging"))
    implementation(project(":ui"))
    implementation(project(":ui-testing"))

    implementation(libs.firebase.functions.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)

    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.play.services.auth)
    implementation(libs.googleid)

    testImplementation(libs.bundles.unit.testing)
    androidTestImplementation(libs.bundles.compose.androidTest)
}
