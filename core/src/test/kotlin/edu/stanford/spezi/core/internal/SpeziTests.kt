package edu.stanford.spezi.core.internal

import android.app.Application
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.ApplicationModule
import edu.stanford.spezi.core.Configuration
import edu.stanford.spezi.core.ConfigurationBuilder
import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.SpeziApplication
import edu.stanford.spezi.core.SpeziError
import edu.stanford.spezi.core.dependency
import edu.stanford.spezi.core.optionalDependency
import edu.stanford.spezi.core.plus
import org.junit.Before
import org.junit.Test

class SpeziTests {

    @Before
    fun setup() {
        SpeziApplication.clear()
    }

    @Test
    fun `it should register application module on configure`() {
        // given
        val application = testApplication {
            // empty
        }

        // when
        val dependency by dependency<ApplicationModule>()

        // then
        assertThat(dependency.application).isEqualTo(application)
    }

    @Test
    fun `it should keep formerly registered application module reconfiguration`() {
        // given
        val application = testApplication { }
        SpeziApplication.configure { }

        // when
        val dependency by dependency<ApplicationModule>()

        // then
        assertThat(dependency.application).isEqualTo(application)
    }

    @Test
    fun `it should invoke configure on registered module`() {
        // given
        testApplication {
            module { ConfigurableModule() }
        }

        // when
        val dependency by dependency<ConfigurableModule>()

        // then
        assertThat(dependency.configured).isTrue()
    }

    @Test
    fun `it should handle interface and impl type registration correclty`() {
        // given
        val implementation = OnboardingImpl()
        testApplication {
            module<Onboarding> { implementation }
        }

        // when
        val onboarding by dependency<Onboarding>()

        // then
        assertThat(onboarding).isEqualTo(implementation)
    }

    @Test
    fun `it should throw a spezi error in case application is not configured yet`() {
        // when
        val applicationModule by dependency<ApplicationModule>()
        val expectedMessage = """
                Spezi is not configured configured yet. Please make sure your main application conforms to [SpeziApplication],
                and you did not request dependencies in the configuration block outside of module factories.
        """.trimMargin()

        // when
        val speziError = runCatching { applicationModule.application }.exceptionOrNull() as SpeziError

        // then
        assertThat(speziError.message).isEqualTo(expectedMessage)
    }

    @Test
    fun `it should return null in case the dependency is not registered on optionalDependency`() {
        // given
        testApplication {
            // no dependencies registered
        }

        // when
        val dependency by optionalDependency<Module1>()

        // then
        assertThat(dependency).isNull()
    }

    @Test
    fun `it should return the same instance on dependency and optionalDependency`() {
        // given
        val module1 = Module1(name = "Module 1")
        testApplication {
            module { module1 }
        }

        // when
        val dependency by dependency<Module1>()
        val optionalDependency by optionalDependency<Module1>()

        // then
        assertThat(dependency).isEqualTo(optionalDependency)
        assertThat(dependency).isEqualTo(module1)
    }

    @Test
    fun `it should handle module registration via identifier correctly`() {
        // given
        val identifier = "module-1-identifier"
        val module1 = Module1(name = "Module 1")
        val module1WithIdentifier = Module1(name = "Module 1 with identifier")
        testApplication {
            module { module1 }
            module(identifier) { module1WithIdentifier }
        }

        // when
        val dependency1 by dependency<Module1>()
        val dependency2 by dependency<Module1>(identifier)

        // then
        assertThat(dependency1).isEqualTo(module1)
        assertThat(dependency2).isEqualTo(module1WithIdentifier)
    }

    @Test
    fun `it should throw spezi error in case the dependency is not registered and return null on optionalDependency`() {
        // given
        testApplication {
            // no dependencies registered
        }
        val expectedMessage =
            "${ModuleKey<Module1>()} not found. Please make sure to register via in the configuration block of your app component"

        // when
        val optionalDependency by optionalDependency<Module1>()
        val dependency by dependency<Module1>()
        val speziError = runCatching { dependency.name }.exceptionOrNull() as SpeziError

        // then
        assertThat(optionalDependency).isNull()
        assertThat(speziError.message).isEqualTo(expectedMessage)
    }

    @Test
    fun `it should register modules from custom configurations correctly`() {
        // given
        val configuration = Configuration {
            module { Module1(name = "Module 1") }
            module { Module2(age = "Module 2") }
        }
        testApplication {
            include(configuration = configuration)
        }

        // when
        val module1 by dependency<Module1>()
        val module2 by dependency<Module2>()

        // then
        assertThat(module1.name).isEqualTo("Module 1")
        assertThat(module2.age).isEqualTo("Module 2")
    }

    @Test
    fun `it should handle building of dependencies within module factory scope correctly`() {
        // given
        val audioModule = AudioModule()
        val customIdentifier = "custom-audio-module-identifier"
        testApplication {
            module { audioModule }
            module(customIdentifier) { AudioModule() }
            module { Preprocessor(module = dependency()) }
            module { CoughModule(audioModule = dependency(), preprocessor = dependency()) }
        }

        // when
        val coughModule by dependency<CoughModule>()
        val audioModuleDependency by dependency<AudioModule>()
        val preprocessorDependency by dependency<Preprocessor>()
        val customAudionModule by dependency<AudioModule>(customIdentifier)

        // then
        assertThat(audioModule).isEqualTo(audioModuleDependency)
        assertThat(audioModule).isNotEqualTo(customAudionModule)
        assertThat(coughModule.audioModule).isEqualTo(audioModule)
        assertThat(coughModule.preprocessor).isEqualTo(preprocessorDependency)
        assertThat(preprocessorDependency.module).isEqualTo(audioModule)
    }

    @Test
    fun `it should resolve internally requested dependencies on modules correctly`() {
        // given
        val module1 = Module1(name = "Module 1")
        val module2 = Module2(age = "Module 2")
        testApplication {
            module { module1 }
            module { module2 }
            module { ModuleAlternative(module1 = dependency(), module2 = dependency()) }
        }

        // when
        val moduleAlternative by dependency<ModuleAlternative>()

        // then
        assertThat(moduleAlternative.module1OrNull).isEqualTo(module1)
        assertThat(moduleAlternative.module2OrNull).isEqualTo(module2)
        assertThat(moduleAlternative.module1).isEqualTo(module1)
        assertThat(moduleAlternative.module2).isEqualTo(module2)
    }

    @Test
    fun `it should handle a complete dependencies graph correctly`() {
        // given
        val customConfiguration = Configuration {
            module { AudioModule() }
            module { Preprocessor(module = dependency()) }
            module {
                CoughModule(
                    audioModule = dependency(),
                    preprocessor = dependency()
                )
            }
        }
        testApplication {
            include(configuration = customConfiguration)
            module<Onboarding> { OnboardingImpl() }
            module { Module1(name = "Module 1") }
            module { Module2(age = "Module 2") }
            module("my-module-x") { ModuleX() }
            module {
                ModuleAlternative(
                    module1 = dependency<Module1>(),
                    module2 = dependency<Module2>()
                )
            }

            module(identifier = "cough-module-with-identifier") { dependency<CoughModule>() }
        }

        // when
        val onboarding by dependency<Onboarding>()
        val module1 by dependency<Module1>()
        val module2 by dependency<Module2>()
        val moduleX by optionalDependency<ModuleX>("my-module-x")
        val moduleAlternative by dependency<ModuleAlternative>()
        val coughModule by dependency<CoughModule>()
        val audioModule by dependency<AudioModule>()
        val preprocessor by dependency<Preprocessor>()
        val coughModuleWithKey by dependency<CoughModule>(identifier = "cough-module-with-identifier")

        // then
        assertThat(onboarding).isInstanceOf(OnboardingImpl::class.java)
        assertThat(module1.name).isEqualTo("Module 1")
        assertThat(module2.age).isEqualTo("Module 2")
        assertThat(moduleAlternative.module1).isEqualTo(module1)
        assertThat(moduleAlternative.module1OrNull).isEqualTo(module1)
        assertThat(moduleAlternative.module2).isEqualTo(module2)
        assertThat(moduleAlternative.module2OrNull).isEqualTo(module2)
        assertThat(coughModule.audioModule).isEqualTo(audioModule)
        assertThat(coughModule.preprocessor).isEqualTo(preprocessor)
        assertThat(coughModule).isEqualTo(coughModuleWithKey)
        assertThat(moduleX).isNotNull()
    }

    @Test
    fun `it should detect circular dependencies and throw during configuration`() {
        // when
        val result = runCatching {
            testApplication {
                module { CircularDep1(circularDep2 = dependency()) }
                module { CircularDep2(circularDep1 = dependency()) }
            }
        }.exceptionOrNull()

        // then
        assertThat(result).isInstanceOf(SpeziError::class.java)
        assertThat(result?.message).contains("Circular dependency detected while resolving:")
    }

    @Test
    fun `it should register merged configurations correctly`() {
        // given
        val config1 = Configuration {
            module { Module1(name = "Module 1") }
        }
        val config2 = Configuration {
            module { Module2(age = "Module 2") }
        }
        testApplication {
            include(config1 + config2)
        }

        // when
        val module1 by dependency<Module1>()
        val module2 by dependency<Module2>()

        // then
        assertThat(module1.name).isEqualTo("Module 1")
        assertThat(module2.age).isEqualTo("Module 2")
    }

    private fun testApplication(
        scope: ConfigurationBuilder.() -> Unit = {},
    ): TestApplication {
        val application = object : TestApplication(scope) {}
        SpeziApplication.configure(application)
        return application
    }
}

class CircularDep1(val circularDep2: CircularDep2) : Module
class CircularDep2(val circularDep1: CircularDep1) : Module

private abstract class TestApplication(
    scope: ConfigurationBuilder.() -> Unit = {},
) : Application(), SpeziApplication {
    override val configuration: Configuration = Configuration(scope = scope)
}

private interface Onboarding : Module
private class OnboardingImpl : Onboarding
private class ModuleX : Module
private class Module1(val name: String) : Module

private class Module2(val age: String) : Module

private class ModuleAlternative(val module1: Module1, val module2: Module2) : Module {
    val module1OrNull by optionalDependency<Module1>()
    val module2OrNull by optionalDependency<Module2>()
}

private class ConfigurableModule : Module {
    var configured: Boolean = false
        private set

    override fun configure() {
        configured = true
    }
}

private class AudioModule : Module
private class Preprocessor(val module: AudioModule) : Module
private class CoughModule(val audioModule: AudioModule, val preprocessor: Preprocessor) : Module
