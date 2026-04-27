package edu.stanford.spezi.core.coroutines

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.testing.concurrency.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import org.junit.Rule
import org.junit.Test

class ConcurrencyTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sut = Concurrency()

    @Test
    fun `it should return the main dispatcher`() {
        assertThat(sut.mainDispatcher()).isEqualTo(Dispatchers.Main)
    }

    @Test
    fun `it should return the main immediate dispatcher`() {
        assertThat(sut.mainImmediateDispatcher()).isEqualTo(Dispatchers.Main.immediate)
    }

    @Test
    fun `it should return the default dispatcher`() {
        assertThat(sut.defaultDispatcher()).isEqualTo(Dispatchers.Default)
    }

    @Test
    fun `it should return the io dispatcher`() {
        assertThat(sut.ioDispatcher()).isEqualTo(Dispatchers.IO)
    }

    @Test
    fun `it should return the unconfined dispatcher`() {
        assertThat(sut.unconfinedDispatcher()).isEqualTo(Dispatchers.Unconfined)
    }

    @Test
    fun `it should return distinct coroutine scopes per dispatcher`() {
        val main = sut.mainCoroutineScope()
        val default = sut.defaultCoroutineScope()
        val io = sut.ioCoroutineScope()
        val unconfined = sut.unconfinedCoroutineScope()

        assertThat(main).isNotSameInstanceAs(default)
        assertThat(main).isNotSameInstanceAs(io)
        assertThat(main).isNotSameInstanceAs(unconfined)
    }

    @Test
    fun `it should return a new scope instance on each call`() {
        val first = sut.defaultCoroutineScope()
        val second = sut.defaultCoroutineScope()

        assertThat(first).isNotSameInstanceAs(second)
    }
}
