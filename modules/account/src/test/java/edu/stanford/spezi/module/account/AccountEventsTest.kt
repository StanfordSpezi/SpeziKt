package edu.stanford.spezi.module.account

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.runTestUnconfined
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test

class AccountEventsTest {

    private lateinit var accountEvents: AccountEvents

    @Before
    fun setUp() {
        accountEvents = AccountEvents(CoroutineScope(Dispatchers.Unconfined))
    }

    @Test
    fun `given SignInSuccess event when emit is called then events should emit SignInSuccess`() =
        runTestUnconfined {
            // Given
            val event = AccountEvents.Event.SignInSuccess

            // When
            accountEvents.emit(event)

            // Then
            val emittedEvent = accountEvents.events.first()
            assertThat(emittedEvent).isEqualTo(AccountEvents.Event.SignInSuccess)
        }

    @Test
    fun `given SignInFailure event when emit is called then events should emit SignInFailure`() =
        runTestUnconfined {
            // Given
            val event = AccountEvents.Event.SignInFailure

            // When
            accountEvents.emit(event)

            // Then
            val emittedEvent = accountEvents.events.first()
            assertThat(emittedEvent).isEqualTo(AccountEvents.Event.SignInFailure)
        }

    @Test
    fun `given SignUpSuccess event when emit is called then events should emit SignUpSuccess`() =
        runTestUnconfined {
            // Given
            val event = AccountEvents.Event.SignUpSuccess

            // When
            accountEvents.emit(event)

            // Then
            val emittedEvent = accountEvents.events.first()
            assertThat(emittedEvent).isEqualTo(AccountEvents.Event.SignUpSuccess)
        }

    @Test
    fun `given SignUpFailure event when emit is called then events should emit SignUpFailure`() =
        runTestUnconfined {
            // Given
            val event = AccountEvents.Event.SignUpFailure

            // When
            accountEvents.emit(event)

            // Then
            val emittedEvent = accountEvents.events.first()
            assertThat(emittedEvent).isEqualTo(AccountEvents.Event.SignUpFailure)
        }
}
