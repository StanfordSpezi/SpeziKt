plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.spezi.hilt)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "edu.stanford.spezi.core.design"

    buildFeatures {
        compose = true
    }

    buildTypes {
        debug {
            // Disabling coverage due to: https://github.com/hapifhir/org.hl7.fhir.core/issues/1688
            enableAndroidTestCoverage = false
        }
    }

    packaging {
        resources {
            excludes += listOf("META-INF/INDEX.LIST")
        }
    }
}

dependencies {
    api(libs.android.fhir.data.capture)

    implementation(project(":core:utils"))

    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.compose)
    implementation(libs.bundles.compose)
    implementation(libs.coil.compose)

    androidTestImplementation(libs.bundles.compose.androidTest)
    androidTestImplementation(composeBom)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    implementation(kotlin("reflect"))
}
