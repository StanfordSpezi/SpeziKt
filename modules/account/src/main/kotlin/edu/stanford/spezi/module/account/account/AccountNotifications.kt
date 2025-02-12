package edu.stanford.spezi.module.account.account

import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.accountId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject

class AccountNotifications @Inject constructor(
    var standard: Standard,
    var storage: ExternalAccountStorage,
) {
    sealed interface Event {
        data class DeletingAccount(val accountId: String) : Event
        data class AssociatedAccount(val details: AccountDetails) : Event
        data class DetailsChanged(val previous: AccountDetails, val new: AccountDetails) : Event
        data class DisassociatingAccount(val details: AccountDetails) : Event
    }

    private val collectors = mutableMapOf<UUID, FlowCollector<Event>>()
    private val mutex = Mutex()

    val events: Flow<Event> get() {
        val id = edu.stanford.spezi.core.utils.UUID()
        return flow<Event> {
            mutex.withLock {
                collectors[id] = this
            }
        }.onCompletion {
            mutex.withLock {
                collectors.remove(id)
            }
        }
    }

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
}
