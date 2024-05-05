import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import edu.stanford.spezikt.build.logic.convention.configureAndroidCompose
import edu.stanford.spezikt.build.logic.convention.disableUnnecessaryAndroidTests
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

class SpeziLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("com.android.library")
        project.plugins.apply("org.jetbrains.kotlin.android")
        val libraryExtension = project.extensions.getByType<LibraryExtension>()

        project.configureAndroidCompose(libraryExtension)

        project.extensions.configure<LibraryAndroidComponentsExtension> {
            disableUnnecessaryAndroidTests(project)
        }
    }
}