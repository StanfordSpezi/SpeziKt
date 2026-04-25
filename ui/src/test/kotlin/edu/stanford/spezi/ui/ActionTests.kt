package edu.stanford.spezi.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ActionTests {

    @Test
    fun `push dispatches action to registered collector`() {
        // given
        val source = ActionSource<TestAction>()
        val received = mutableListOf<TestAction>()
        source.collect { received.add(it) }

        // when
        source.sink<TestAction>().push(TestAction.Refresh)

        // then
        assertThat(received).containsExactly(TestAction.Refresh)
    }

    @Test
    fun `push dispatches action to all registered collectors in order`() {
        // given
        val source = ActionSource<TestAction>()
        val log = mutableListOf<String>()
        source.collect { log.add("first") }
        source.collect { log.add("second") }

        // when
        source.sink<TestAction>().push(TestAction.Refresh)

        // then
        assertThat(log).containsExactly("first", "second").inOrder()
    }

    @Test
    fun `push with no collectors does not throw`() {
        // given
        val source = ActionSource<TestAction>()

        // when / then — no exception expected
        source.sink<TestAction>().push(TestAction.Refresh)
    }

    @Test
    fun `push multiple actions are each delivered to collector`() {
        // given
        val source = ActionSource<TestAction>()
        val received = mutableListOf<TestAction>()
        source.collect { received.add(it) }

        // when
        source.sink<TestAction>().push(TestAction.Refresh)
        source.sink<TestAction>().push(TestAction.Dismiss)

        // then
        assertThat(received)
            .containsExactly(TestAction.Refresh, TestAction.Dismiss)
            .inOrder()
    }

    @Test
    fun `constructor with collector registers it immediately`() {
        // given
        val received = mutableListOf<TestAction>()
        val source = ActionSource<TestAction> { received.add(it) }

        // when
        source.sink<TestAction>().push(TestAction.Refresh)

        // then
        assertThat(received).containsExactly(TestAction.Refresh)
    }

    @Test
    fun `clear removes all collectors so subsequent pushes are not delivered`() {
        // given
        val source = ActionSource<TestAction>()
        val received = mutableListOf<TestAction>()
        source.collect { received.add(it) }
        source.sink<TestAction>().push(TestAction.Refresh)

        // when
        source.clear()
        source.sink<TestAction>().push(TestAction.Dismiss)

        // then — only the pre-clear push was delivered
        assertThat(received).containsExactly(TestAction.Refresh)
    }

    @Test
    fun `collect after clear re-registers a new collector`() {
        // given
        val source = ActionSource<TestAction>()
        val received = mutableListOf<TestAction>()
        source.collect { received.add(it) }
        source.clear()

        // when
        source.collect { received.add(it) }
        source.sink<TestAction>().push(TestAction.Refresh)

        // then
        assertThat(received).containsExactly(TestAction.Refresh)
    }

    @Test
    fun `sink typed to subtype only accepts actions of that subtype`() {
        // given
        val source = ActionSource<TestAction>()
        val received = mutableListOf<TestAction>()
        source.collect { received.add(it) }
        val subtypeSink: ActionSink<TestAction.ShowToast> = source.sink()

        // when
        subtypeSink.push(TestAction.ShowToast("hello"))

        // then
        assertThat(received).containsExactly(TestAction.ShowToast("hello"))
    }

    private sealed interface TestAction {
        data object Refresh : TestAction
        data object Dismiss : TestAction
        data class ShowToast(val message: String) : TestAction
    }
}
