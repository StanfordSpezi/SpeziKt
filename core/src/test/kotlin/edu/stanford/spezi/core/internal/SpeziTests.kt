package edu.stanford.spezi.core.internal

import android.content.Context
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.ApplicationModule
import edu.stanford.spezi.core.Configuration
import edu.stanford.spezi.core.DefaultInitializer
import edu.stanford.spezi.core.Module
import edu.stanford.spezi.core.SpeziApplication
import edu.stanford.spezi.core.SpeziError
import edu.stanford.spezi.core.Standard
import edu.stanford.spezi.core.dependency
import edu.stanford.spezi.core.optionalDependency
import edu.stanford.spezi.core.plus
import edu.stanford.spezi.core.requireDependency
import edu.stanford.spezi.core.requireOptionalDependency
import edu.stanford.spezi.testing.core.TestStandard
import edu.stanford.spezi.testing.core.testSpeziApplication
import org.junit.Test

class SpeziTests {

    @Test
    fun `it should register application module on configure`() {
        // given
        val application = testSpeziApplication {
            // empty
        }

        // when
        val dependency = dependency<ApplicationModule>().value

        // then
        assertThat(dependency.application).isEqualTo(application)
    }

    @Test
    fun `it should register use correct standard`() {
        // given
        val testStandard = object : Standard {}
        testSpeziApplication(standard = testStandard) {
            // empty
        }

        // when
        val dependency = dependency<ApplicationModule>().value

        // then
        assertThat(dependency.standard).isEqualTo(testStandard)
    }

    @Test
    fun `it should be able to create and return modules with empty constructor`() {
        // given
        testSpeziApplication {
            // no dependencies registered
        }

        // when
        val dependency by dependency<EmptyModule>()

        // then
        assertThat(dependency.name).isEqualTo("empty-module")
    }

    @Test
    fun `it should be able to create and return modules with context constructor`() {
        // given
        testSpeziApplication {
            // no dependencies registered
        }

        // when
        val dependency by dependency<EmptyModuleContext>()

        // then
        assertThat(dependency.name).isEqualTo("empty-module-context")
    }

    @Test
    fun `it should be able to create and return modules from Default initializer of companion object`() {
        // given
        val application = testSpeziApplication {
            // no dependencies registered
        }

        // when
        val dependency by dependency<EmptyModuleWithCompanion>()
        val defaultInstance = EmptyModuleWithCompanion.create(application)

        // then
        assertThat(dependency).isEqualTo(defaultInstance)
    }

    @Test
    fun `it should keep formerly registered application module reconfiguration`() {
        // given
        val application = testSpeziApplication { }
        SpeziApplication.configure(application.configuration.standard) { }

        // when
        val dependency by dependency<ApplicationModule>()

        // then
        assertThat(dependency.application).isEqualTo(application)
    }

    @Test
    fun `it should invoke configure on registered module`() {
        // given
        testSpeziApplication {
            module { ConfigurableModule() }
        }

        // when
        val dependency by dependency<ConfigurableModule>()

        // then
        assertThat(dependency.configured).isTrue()
    }

    @Test
    fun `it should handle interface and impl type registration correctly`() {
        // given
        val implementation = OnboardingImpl()
        testSpeziApplication {
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
        SpeziApplication.clear()
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
        testSpeziApplication {
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
        testSpeziApplication {
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
        testSpeziApplication {
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
        testSpeziApplication {
            // no dependencies registered
        }
        val expectedMessage =
            "${DependencyKey<Module1>()} not found. Please make sure to register it in the configuration block of your app."

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
        val configuration = Configuration(standard = TestStandard) {
            module { Module1(name = "Module 1") }
            module { Module2(age = "Module 2") }
        }
        testSpeziApplication {
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
        testSpeziApplication {
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
        testSpeziApplication {
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
        val customConfiguration = Configuration(standard = TestStandard) {
            module { AudioModule() }
            module { Preprocessor(module = dependency()) }
            module {
                CoughModule(
                    audioModule = dependency(),
                    preprocessor = dependency()
                )
            }
        }
        testSpeziApplication {
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
            testSpeziApplication {
                module { CircularDep1(circularDep2 = dependency()) }
                module { CircularDep2(circularDep1 = dependency()) }
            }
        }.exceptionOrNull()

        // then
        assertThat(result).isInstanceOf(SpeziError::class.java)
        assertThat(result?.message).contains("Circular dependency detected while resolving:")
    }

    @Test
    fun `it should resolve a singleton non-module dependency`() {
        // given
        val service = UserService(name = "Alice")
        testSpeziApplication {
            singleton { service }
        }

        // when
        val resolved = requireDependency<UserService>()
        val optionalResolved = requireOptionalDependency<UserService>()

        // then
        assertThat(resolved).isEqualTo(service)
        assertThat(optionalResolved).isEqualTo(service)
    }

    @Test
    fun `it should return the same singleton instance on every resolution`() {
        // given
        testSpeziApplication {
            singleton { UserService(name = "Bob") }
        }

        // when
        val first = requireDependency<UserService>()
        val second = requireDependency<UserService>()

        // then
        assertThat(first).isSameInstanceAs(second)
    }

    @Test
    fun `it should create a new instance on every factory resolution`() {
        // given
        testSpeziApplication {
            factory { UserService(name = "transient") }
        }

        // when
        val first = requireDependency<UserService>()
        val second = requireDependency<UserService>()

        // then
        assertThat(first).isNotSameInstanceAs(second)
        assertThat(first.name).isEqualTo(second.name)
    }

    @Test
    fun `it should allow singleton to resolve other registered dependencies`() {
        // given
        val module1 = Module1(name = "Module 1")
        testSpeziApplication {
            module { module1 }
            singleton { UserService(name = dependency<Module1>().name) }
        }

        // when
        val resolved = requireDependency<UserService>()

        // then
        assertThat(resolved.name).isEqualTo("Module 1")
    }

    @Test
    fun `it should be able to create and return non-module types with empty constructor`() {
        // given
        testSpeziApplication {
            // no dependencies registered
        }

        // when
        val dependency by dependency<PlainServiceNoArg>()

        // then
        assertThat(dependency.label).isEqualTo("plain-no-arg")
    }

    @Test
    fun `it should be able to create and return non-module types with context constructor`() {
        // given
        testSpeziApplication {
            // no dependencies registered
        }

        // when
        val dependency by dependency<PlainServiceContext>()

        // then
        assertThat(dependency.label).isEqualTo("plain-context")
    }

    @Test
    fun `it should be able to create and return non-module types from DefaultInitializer companion`() {
        // given
        val application = testSpeziApplication {
            // no dependencies registered
        }

        // when
        val dependency by dependency<PlainServiceWithCompanion>()
        val defaultInstance = PlainServiceWithCompanion.create(application)

        // then
        assertThat(dependency).isEqualTo(defaultInstance)
    }

    @Test
    fun `it should return null for unregistered non-module dependency`() {
        // given
        testSpeziApplication { }

        // when
        val resolved = requireOptionalDependency<UserService>()

        // then
        assertThat(resolved).isNull()
    }

    @Test
    fun `it should register merged configurations correctly`() {
        // given
        val config1 = Configuration(standard = TestStandard) {
            module { Module1(name = "Module 1") }
        }
        val config2 = Configuration(standard = TestStandard) {
            module { Module2(age = "Module 2") }
        }
        testSpeziApplication {
            include(config1 + config2)
        }

        // when
        val module1 by dependency<Module1>()
        val module2 by dependency<Module2>()

        // then
        assertThat(module1.name).isEqualTo("Module 1")
        assertThat(module2.age).isEqualTo("Module 2")
    }
}

class CircularDep1(val circularDep2: CircularDep2) : Module
class CircularDep2(val circularDep1: CircularDep1) : Module

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
class EmptyModule : Module {
    val name = "empty-module"
}
class EmptyModuleContext(private val context: Context) : Module {
    val name = "empty-module-context"
}
data class EmptyModuleWithCompanion(val name: String) : Module {
    companion object : DefaultInitializer<EmptyModuleWithCompanion> {
        override fun create(context: Context): EmptyModuleWithCompanion {
            return EmptyModuleWithCompanion(name = "empty-module-with-companion")
        }
    }
}

/** A plain (non-[Module]) class used to test singleton / factory registrations. */
data class UserService(val name: String)

/** A plain (non-[Module]) class with a no-arg constructor for [DefaultInitializer] fallback tests. */
class PlainServiceNoArg {
    val label = "plain-no-arg"
}

/** A plain (non-[Module]) class with a [Context]-arg constructor for [DefaultInitializer] fallback tests. */
data class PlainServiceContext(private val context: Context) {
    val label = "plain-context"
}

/** A plain (non-[Module]) class with a [DefaultInitializer] companion for fallback tests. */
data class PlainServiceWithCompanion(val label: String) {
    companion object : DefaultInitializer<PlainServiceWithCompanion> {
        override fun create(context: Context) = PlainServiceWithCompanion(label = "plain-companion")
    }
}
