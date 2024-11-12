package edu.stanford.spezi.module.account.account

import android.text.style.TabStopSpan.Standard
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.accountId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

class AccountNotifications {
    sealed interface Event {
        data class DeletingAccount(val accountId: String) : Event
        data class AssociatedAccount(val details: AccountDetails) : Event
        data class DetailsChanged(val previous: AccountDetails, val new: AccountDetails) : Event
        data class DisassociatingAccount(val details: AccountDetails) : Event
    }

    private val standard: Standard = TODO()
    private val storage: ExternalAccountStorage = TODO()
    private val collectors = mutableMapOf<UUID, FlowCollector<Event>>()
    private val mutex = Mutex()

    val events: Flow<Event> get() = newSubscription()

    suspend fun reportEvent(event: Event) {
        (standard as? AccountNotifyConstraint)?.respondToEvent(event)

        when (event) {
            is Event.DeletingAccount -> {
                storage.willDeleteAccount(event.accountId)
            }
            is Event.DisassociatingAccount -> {
                storage.userWillDisassociate(event.details.accountId)
            }
            else -> {}
        }

        mutex.withLock {
            for (collector in collectors.values) {
                collector.emit(event)
            }
        }
    }

    private fun newSubscription(): Flow<Event> {
        val key = UUID.randomUUID()
        return flow {
            mutex.withLock {
                collectors[key] = this
            }
        }.onCompletion {
            mutex.withLock {
                collectors.remove(key)
            }
        }
    }
}
