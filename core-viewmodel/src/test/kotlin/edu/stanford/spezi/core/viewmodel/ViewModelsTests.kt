package edu.stanford.spezi.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.SpeziError
import edu.stanford.spezi.core.requireDependency
import edu.stanford.spezi.core.viewmodel.internal.ViewModelFactories
import edu.stanford.spezi.testing.core.testSpeziApplication
import org.junit.Test

class ViewModelsTests {

    @Test
    fun `it should register a single viewModel factory`() {
        // given
        testSpeziApplication {
            viewModel { CounterViewModel() }
        }

        // when
        val factories = requireDependency<ViewModelFactories>()
        val instance = factories.create(CounterViewModel::class, SavedStateHandle())

        // then
        assertThat(instance).isInstanceOf(CounterViewModel::class.java)
    }

    @Test
    fun `it should accumulate factories across multiple viewModel calls`() {
        // given
        testSpeziApplication {
            viewModel { CounterViewModel() }
            viewModel { MessageViewModel() }
        }

        // when
        val factories = requireDependency<ViewModelFactories>()
        val counter = factories.create(CounterViewModel::class, SavedStateHandle())
        val message = factories.create(MessageViewModel::class, SavedStateHandle())

        // then
        assertThat(counter).isInstanceOf(CounterViewModel::class.java)
        assertThat(message).isInstanceOf(MessageViewModel::class.java)
    }

    @Test
    fun `it should override a factory when the same ViewModel type is registered twice`() {
        // given
        testSpeziApplication {
            viewModel { MessageViewModel(text = "first") }
            viewModel { MessageViewModel(text = "second") }
        }

        // when
        val factories = requireDependency<ViewModelFactories>()
        val instance = factories.create(MessageViewModel::class, SavedStateHandle())

        // then
        assertThat(instance.text).isEqualTo("second")
    }

    @Test
    fun `it should resolve a module dependency inside a viewModel factory`() {
        // given
        val repo = UserRepository(name = "Alice")
        testSpeziApplication {
            singleton { repo }
            viewModel { ProfileViewModel(repository = dependency()) }
        }

        // when
        val factories = requireDependency<ViewModelFactories>()
        val vm = factories.create(ProfileViewModel::class, SavedStateHandle())

        // then
        assertThat(vm.repository).isEqualTo(repo)
    }

    @Test
    fun `it should resolve an optional dependency inside a viewModel factory`() {
        // given
        testSpeziApplication {
            viewModel { ProfileViewModel(repository = optionalDependency()) }
        }

        // when
        val factories = requireDependency<ViewModelFactories>()
        val vm = factories.create(ProfileViewModel::class, SavedStateHandle())

        // then
        assertThat(vm.repository).isNull()
    }

    @Test
    fun `it should expose the savedStateHandle inside a viewModel factory`() {
        // given
        val handle = SavedStateHandle(mapOf("id" to "42"))
        testSpeziApplication {
            viewModel { DetailViewModel(handle = savedStateHandle()) }
        }

        // when
        val factories = requireDependency<ViewModelFactories>()
        val vm = factories.create(DetailViewModel::class, handle)

        // then
        assertThat(vm.handle.get<String>("id")).isEqualTo("42")
    }

    @Test
    fun `it should throw a spezi error when creating an unregistered viewModel`() {
        // given
        testSpeziApplication {
            viewModel { CounterViewModel() }
        }

        // when
        val factories = requireDependency<ViewModelFactories>()
        val error = runCatching {
            factories.create(MessageViewModel::class, SavedStateHandle())
        }.exceptionOrNull() as? SpeziError

        // then
        assertThat(error).isNotNull()
        assertThat(error?.message).contains("MessageViewModel")
    }
}

private class CounterViewModel : ViewModel()

private class MessageViewModel(val text: String = "") : ViewModel()

private data class UserRepository(val name: String)

private class ProfileViewModel(val repository: UserRepository?) : ViewModel()

private class DetailViewModel(val handle: SavedStateHandle) : ViewModel()
