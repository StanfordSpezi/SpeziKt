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
include(":contact")
include(":core")
include(":core-coroutines")
include(":core-logging")
include(":foundation")
include(":modules:account")
include(":modules:bluetooth")
include(":modules:design")
include(":modules:education")
include(":modules:healthconnectonfhir")
include(":modules:logging")
include(":modules:navigation")
include(":modules:notification")
include(":modules:onboarding")
include(":modules:testing")
include(":modules:utils")
include(":questionnaire")
include(":storage-credential")
include(":storage-local")
include(":ui")
include(":ui-markdown")
include(":ui-personalinfo")
include(":ui-testing")
include(":ui-validation")
include(":core-testing")
