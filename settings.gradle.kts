pluginManagement {
    includeBuild("build-logic")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SpeziKt"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Please keep the projects sorted. Select all method calls below and in Android Studio `Edit > Sort Lines`
include(":app")
include(":core:bluetooth")
include(":core:coroutines")
include(":core:design")
include(":core:logging")
include(":core:testing")
include(":core:utils")
include(":modules:contact")
include(":modules:onboarding")
include(":modules:account")
include(":core:navigation")
include(":modules:storage")
