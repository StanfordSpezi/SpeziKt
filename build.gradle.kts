import org.jetbrains.dokka.DokkaConfiguration
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    id("org.jetbrains.dokka") version "1.9.20"
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")


    if (this != rootProject) {
        rootProject.tasks.named("dokkaHtmlMultiModule") {
            dependsOn("${project.path}:dokkaHtml")
        }
    }

    tasks.withType<DokkaTaskPartial>().configureEach {
        dokkaSourceSets.configureEach {
            documentedVisibilities.set(
                setOf(
                    DokkaConfiguration.Visibility.PUBLIC,
                    DokkaConfiguration.Visibility.PROTECTED
                )
            )
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

tasks.dokkaHtmlMultiModule {
    moduleName.set("SpeziKt Documentation")
}