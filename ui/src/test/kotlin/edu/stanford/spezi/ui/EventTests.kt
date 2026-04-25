package edu.stanford.spezi.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EventTests {

    @Test
    fun `push single event is consumable`() {
        // given
        val sink = EventSink<TestEvent>()

        // when
        sink.push(TestEvent.NavigateHome)

        // then
        assertThat(sink.source().consumeAll()).containsExactly(TestEvent.NavigateHome)
    }

    @Test
    fun `push multiple distinct event types preserves all of them`() {
        // given
        val sink = EventSink<TestEvent>()

        // when
        sink.push(TestEvent.NavigateHome)
        sink.push(TestEvent.Dismiss)

        // then
        assertThat(sink.source().consumeAll())
            .containsExactly(TestEvent.NavigateHome, TestEvent.Dismiss)
            .inOrder()
    }

    @Test
    fun `push same event type twice keeps only the latest`() {
        // given
        val sink = EventSink<TestEvent>()

        // when
        sink.push(TestEvent.ShowToast("first"))
        sink.push(TestEvent.ShowToast("second"))

        // then
        assertThat(sink.source().consumeAll())
            .containsExactly(TestEvent.ShowToast("second"))
    }

    @Test
    fun `push replaces existing event of same type regardless of position in queue`() {
        // given
        val sink = EventSink<TestEvent>()
        sink.push(TestEvent.ShowToast("old"))
        sink.push(TestEvent.Dismiss)

        // when — push a new ShowToast; the old one should be removed, Dismiss stays
        sink.push(TestEvent.ShowToast("new"))

        // then
        assertThat(sink.source().consumeAll())
            .containsExactly(TestEvent.Dismiss, TestEvent.ShowToast("new"))
            .inOrder()
    }

    @Test
    fun `pushAll enqueues all distinct events`() {
        // given
        val sink = EventSink<TestEvent>()

        // when
        sink.pushAll(listOf(TestEvent.NavigateHome, TestEvent.Dismiss))

        // then
        assertThat(sink.source().consumeAll())
            .containsExactly(TestEvent.NavigateHome, TestEvent.Dismiss)
            .inOrder()
    }

    @Test
    fun `pushAll deduplicates within the batch keeping the last occurrence`() {
        // given
        val sink = EventSink<TestEvent>()

        // when — two ShowToast events in same batch; only the last should survive
        sink.pushAll(listOf(TestEvent.ShowToast("first"), TestEvent.ShowToast("second")))

        // then
        assertThat(sink.source().consumeAll())
            .containsExactly(TestEvent.ShowToast("second"))
    }

    @Test
    fun `pushAll deduplicates against already queued events`() {
        // given
        val sink = EventSink<TestEvent>()
        sink.push(TestEvent.ShowToast("existing"))

        // when
        sink.pushAll(listOf(TestEvent.NavigateHome, TestEvent.ShowToast("updated")))

        // then — existing ShowToast replaced, NavigateHome added
        assertThat(sink.source().consumeAll())
            .containsExactly(TestEvent.NavigateHome, TestEvent.ShowToast("updated"))
            .inOrder()
    }

    @Test
    fun `pushAll with empty list does not change the queue`() {
        // given
        val sink = EventSink<TestEvent>()
        sink.push(TestEvent.Dismiss)

        // when
        sink.pushAll(emptyList())

        // then
        assertThat(sink.source().consumeAll()).containsExactly(TestEvent.Dismiss)
    }

    @Test
    fun `consumeAll returns empty list when no events were pushed`() {
        // given
        val sink = EventSink<TestEvent>()

        // when
        val events = sink.source().consumeAll()

        // then
        assertThat(events).isEmpty()
    }

    @Test
    fun `consumeAll drains the queue so a second call returns empty`() {
        // given
        val sink = EventSink<TestEvent>()
        sink.push(TestEvent.NavigateHome)

        // when
        sink.source().consumeAll()
        val secondDrain = sink.source().consumeAll()

        // then
        assertThat(secondDrain).isEmpty()
    }

    @Test
    fun `source emits new snapshot after each push`() {
        // given
        val sink = EventSink<TestEvent>()

        // when
        sink.push(TestEvent.NavigateHome)
        val firstSnapshot = sink.source().consumeAll()
        sink.push(TestEvent.Dismiss)
        val secondSnapshot = sink.source().consumeAll()

        // then
        assertThat(firstSnapshot).containsExactly(TestEvent.NavigateHome)
        assertThat(secondSnapshot).containsExactly(TestEvent.Dismiss)
    }

    @Test
    fun `source consume invokes handler for each pending event in order`() {
        // given
        val sink = EventSink<TestEvent>()
        sink.push(TestEvent.NavigateHome)
        sink.push(TestEvent.Dismiss)
        val consumed = mutableListOf<TestEvent>()

        // when
        sink.source().value.consume { event ->
            consumed.add(event)
        }

        // then
        assertThat(consumed)
            .containsExactly(TestEvent.NavigateHome, TestEvent.Dismiss)
            .inOrder()
    }

    private sealed interface TestEvent {
        data object NavigateHome : TestEvent
        data object Dismiss : TestEvent
        data class ShowToast(val message: String) : TestEvent
    }
}
