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
include(":account")
include(":account-firebase")
include(":contact")
include(":core")
include(":core-coroutines")
include(":core-lifecycle")
include(":core-logging")
include(":core-testing")
include(":core-time")
include(":foundation")
include(":health")
include(":questionnaire")
include(":sample-app")
include(":storage-credential")
include(":storage-local")
include(":testing-ui")
include(":testing-screenshot")
include(":testing-concurrency")
include(":ui")
include(":ui-account")
include(":ui-markdown")
include(":ui-personalinfo")
include(":ui-theme")
include(":ui-validation")
