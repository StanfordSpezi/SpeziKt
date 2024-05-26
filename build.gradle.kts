import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) version libs.versions.kotlin apply false
    alias(libs.plugins.detekt) version libs.versions.detekt
    alias(libs.plugins.dokka) version libs.versions.dokka
    alias(libs.plugins.google.devtools.ksp) version libs.versions.kspVersion apply false
    alias(libs.plugins.hilt.android) version libs.versions.hiltVersion apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

subprojects { 
    setupDokka()
    setupDetekt()
}

tasks.dokkaHtmlMultiModule {
    moduleName.set("Spezi Documentation")
}

fun Project.setupDokka() {
    apply(plugin = rootProject.libs.plugins.dokka.get().pluginId)

    if (this != rootProject) {
        rootProject.tasks.named("dokkaHtmlMultiModule") {
            dependsOn("${project.path}:dokkaHtml")
        }
    }

    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            noAndroidSdkLink.set(false)
            skipDeprecated.set(true)
            skipEmptyPackages.set(true)
            includeNonPublic.set(false)
            jdkVersion.set(17)
            if (file("README.md").exists()) {
                includes.from("README.md")
            }
        }
    }

    val dokkaHtmlMultiModule = tasks.findByName("dokkaHtmlMultiModule") ?: tasks.create(
        "dokkaHtmlMultiModule",
        DokkaTaskPartial::class.java
    )
    rootProject.tasks.named("dokkaHtmlMultiModule") {
        dependsOn(dokkaHtmlMultiModule)
    }
}

fun Project.setupDetekt() {
    val libs = rootProject.libs
    apply(plugin = libs.plugins.detekt.get().pluginId)
    detekt {
        toolVersion = libs.versions.detekt.get()
        config.setFrom("$rootDir/config/detekt-config.yml")
        autoCorrect = true
    }

    dependencies {
        detektPlugins(libs.detekt.formatting)

    }

    tasks.withType<Detekt> {
        reports {
            xml.required.set(true)
            html.required.set(true)
            txt.required.set(true)
            sarif.required.set(true)
        }
    }
}