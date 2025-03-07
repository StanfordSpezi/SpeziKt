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
include(":core:navigation")
include(":core:notification")
include(":core:testing")
include(":core:utils")
include(":modules:account")
include(":modules:contact")
include(":modules:education")
include(":modules:healthconnectonfhir")
include(":modules:onboarding")
include(":modules:storage")
include(":spezi:foundation")
include(":spezi:ui")
include(":spezi:contact")
include(":spezi:validation")
include(":spezi:questionnaire")
include(":spezi:personalinfo")
include(":spezi:localstorage")
include(":spezi:keystorage")
include(":spezi:credentialstorage")
include(":spezi:core")
include(":spezi:onboarding")
include(":spezi:onboarding")
