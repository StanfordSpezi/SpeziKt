plugins {
    alias(libs.plugins.spezi.library)
}

android {
    namespace = "edu.stanford.spezi.account.firebase"
}

dependencies {
    api(project(":account"))
    api(libs.firebase.auth.ktx)
    api(libs.firebase.firestore.ktx)

    implementation(project(":core-coroutines"))
    implementation(libs.googleid)
}
