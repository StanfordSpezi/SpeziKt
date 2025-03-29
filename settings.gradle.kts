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

var isClaidDeveloper: Boolean
val homeDir = System.getProperty("user.home")
var credentialsFile = file("${homeDir}/.claid/developer_settings.txt")

val properties = java.util.Properties()

if (!credentialsFile.exists()) {
    credentialsFile = file("developer_settings.txt")
}

if (credentialsFile.exists()) {
    credentialsFile.inputStream().use { stream ->
        properties.load(stream)
    }
    isClaidDeveloper = true  // Set to true if credentials file is found
    println("You are a CLAID developer!")
} else {
    isClaidDeveloper = false  // Keep false if file is not found
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()

        if (isClaidDeveloper) {
            maven {
                name = "CLAID SDK Repo"  // Give the repository a name
                url = uri(properties.getProperty("repo_url") ?: "http://invalid_host_in_claid_developer_settings_file") // Read the URL from the properties file
                isAllowInsecureProtocol = true
                credentials {
                    username = properties.getProperty("developer_name") ?: ""
                    password = properties.getProperty("developer_password") ?: ""
                }
            }
        }
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
include(":modules:speziclaid")
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
include(":claid_cough_detection")

project(":claid_cough_detection").projectDir = File("../CLAIDPackages/claid_cough_detection/packaging/android/claid_cough_detection")