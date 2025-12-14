package edu.stanford.spezi.core.coroutines

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.junit.Before
import org.junit.Test

class CoroutinesModuleTest {
    private val sut = spyk(CoroutinesModule())

    private val dispatchersProvider: DispatchersProvider = mockk()
    private val dispatcher: CoroutineDispatcher = mockk()
    private val coroutineScope: CoroutineScope = mockk()

    @Before
    fun setup() {
        every { sut.buildCoroutine(dispatcher) } returns coroutineScope
    }

    @Test
    fun `it should return the correct main dispatcher`() {
        // given
        every { dispatchersProvider.main() } returns dispatcher

        // when
        val result = sut.provideMainDispatcher(dispatchersProvider)

        // then
        assertThat(result).isEqualTo(dispatcher)
    }

    @Test
    fun `it should return the correct default dispatcher`() {
        // given
        every { dispatchersProvider.default() } returns dispatcher

        // when
        val result = sut.provideDefaultDispatcher(dispatchersProvider)

        // then
        assertThat(result).isEqualTo(dispatcher)
    }

    @Test
    fun `it should return the correct io dispatcher`() {
        // given
        every { dispatchersProvider.io() } returns dispatcher

        // when
        val result = sut.provideIODispatcher(dispatchersProvider)

        // then
        assertThat(result).isEqualTo(dispatcher)
    }

    @Test
    fun `it should return the correct unconfined dispatcher`() {
        // given
        every { dispatchersProvider.unconfined() } returns dispatcher

        // when
        val result = sut.provideUnconfinedDispatcher(dispatchersProvider)

        // then
        assertThat(result).isEqualTo(dispatcher)
    }

    @Test
    fun `it should build the correct main coroutine scope`() {
        // given
        every { dispatchersProvider.main() } returns dispatcher
        every { sut.buildCoroutine(dispatcher) } returns coroutineScope

        // when
        val result = sut.provideMainCoroutineScope(dispatchersProvider)

        // then
        assertThat(result).isEqualTo(coroutineScope)
        verify { sut.buildCoroutine(dispatcher) }
    }

    @Test
    fun `it should build the correct default coroutine scope`() {
        // given
        every { dispatchersProvider.default() } returns dispatcher
        every { sut.buildCoroutine(dispatcher) } returns coroutineScope

        // when
        val result = sut.provideDefaultCoroutineScope(dispatchersProvider)

        // then
        assertThat(result).isEqualTo(coroutineScope)
        verify { sut.buildCoroutine(dispatcher) }
    }

    @Test
    fun `it should build the correct IO coroutine scope`() {
        // given
        every { dispatchersProvider.io() } returns dispatcher
        every { sut.buildCoroutine(dispatcher) } returns coroutineScope

        // when
        val result = sut.provideIOCoroutineScope(dispatchersProvider)

        // then
        assertThat(result).isEqualTo(coroutineScope)
        verify { sut.buildCoroutine(dispatcher) }
    }

    @Test
    fun `it should build the correct Unconfined coroutine scope`() {
        // given
        every { dispatchersProvider.unconfined() } returns dispatcher
        every { sut.buildCoroutine(dispatcher) } returns coroutineScope

        // when
        val result = sut.provideUnconfinedCoroutineScope(dispatchersProvider)

        // then
        assertThat(result).isEqualTo(coroutineScope)
        verify { sut.buildCoroutine(dispatcher) }
    }
}
