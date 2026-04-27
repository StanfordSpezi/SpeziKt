plugins {
    alias(libs.plugins.spezi.library)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "edu.stanford.spezi.core.viewmodel"

    buildFeatures {
        compose = true
    }
}

dependencies {
    api(project(":core"))
    api(libs.androidx.lifecycle.view.model.ktx)
    api(libs.androidx.lifecycle.viewmodel.savedstate)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.androidx.fragment.ktx)
    api(libs.androidx.activity.compose)
    api(libs.compose.runtime)
    testImplementation(project(":testing-core"))
}
