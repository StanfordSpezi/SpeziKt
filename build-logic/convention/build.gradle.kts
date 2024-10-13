import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "edu.stanford.spezi.build.logic"

val javaVersion = JavaVersion.VERSION_17

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = javaVersion.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.hilt.gradle)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

fun NamedDomainObjectContainer<PluginDeclaration>.conventionPlugin(id: String, className: String) {
    register(className) {
        this.id = "spezi.$id"
        implementationClass = "edu.stanford.spezi.build.logic.convention.plugins.$className"
    }
}

gradlePlugin {
    plugins {
        // Please keep plugins sorted. Select all method calls below and in Android Studio `Edit > Sort Lines`
        conventionPlugin(id = "application", className = "SpeziApplicationConventionPlugin")
        conventionPlugin(id = "base", className = "SpeziBaseConfigConventionPlugin")
        conventionPlugin(id = "compose", className = "SpeziComposeConventionPlugin")
        conventionPlugin(id = "hilt", className = "HiltConventionPlugin")
        conventionPlugin(id = "library", className = "SpeziLibraryConventionPlugin")
        conventionPlugin(id = "serialization", className = "SpeziSerializationConventionPlugin")
    }
}
