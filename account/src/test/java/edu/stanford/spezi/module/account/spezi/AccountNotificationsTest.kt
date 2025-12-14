package edu.stanford.spezi.module.account.spezi

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.module.account.account.AccountNotifications
import edu.stanford.spezi.module.account.account.AccountNotifyConstraint
import edu.stanford.spezi.module.account.account.AccountStorageProvider
import edu.stanford.spezi.module.account.account.ExternalAccountStorage
import edu.stanford.spezi.module.account.account.Standard
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountModifications
import edu.stanford.spezi.module.account.account.value.keys.accountId
import kotlinx.coroutines.flow.firstOrNull
import org.junit.Test

private object TestStandard : Standard, AccountNotifyConstraint {
    val trackedEvents = mutableListOf<AccountNotifications.Event>()

    override fun respondToEvent(event: AccountNotifications.Event) {
        trackedEvents.add(event)
    }
}

private object TestProvider : AccountStorageProvider {
    val disassociations = mutableListOf<String>()
    val deletions = mutableListOf<String>()

    override suspend fun load(
        accountId: String,
        keys: List<AccountKey<*>>,
    ): AccountDetails? {
        TODO("Not yet implemented")
    }

    override suspend fun store(accountId: String, modifications: AccountModifications) {
        TODO("Not yet implemented")
    }

    override suspend fun disassociate(accountId: String) {
        disassociations.add(accountId)
    }

    override suspend fun delete(accountId: String) {
        deletions.add(accountId)
    }
}

class AccountNotificationsTest {

    @Test
    fun testAccountNotifications() = runTestUnconfined {
        val externalAccountStorage = ExternalAccountStorage(TestProvider)
        val notifications = AccountNotifications(
            TestStandard,
            externalAccountStorage
        )

        val stream = notifications.events
        val details = mockAccountDetails()

        notifications.reportEvent(AccountNotifications.Event.DeletingAccount("account1"))
        notifications.reportEvent(AccountNotifications.Event.AssociatedAccount(details))
        notifications.reportEvent(AccountNotifications.Event.DisassociatingAccount(details))

        val element0 = stream.firstOrNull()
        val element1 = stream.firstOrNull()
        val element2 = stream.firstOrNull()

        assertEvents(
            element0,
            element1,
            element2,
            details
        )

        assertThat(TestProvider.disassociations).isEqualTo(listOf(details.accountId))
        assertThat(TestProvider.deletions).isEqualTo(listOf(details.accountId))

        assertThat(TestStandard.trackedEvents).hasSize(3)
        assertEvents(
            TestStandard.trackedEvents[0],
            TestStandard.trackedEvents[1],
            TestStandard.trackedEvents[2],
            details
        )
    }
}

private fun assertEvents(
    event0: AccountNotifications.Event?,
    event1: AccountNotifications.Event?,
    event2: AccountNotifications.Event?,
    details: AccountDetails,
) {
    (event0 as? AccountNotifications.Event.DeletingAccount)?.let {
        assertThat(it.accountId).isEqualTo(details.accountId)
    } ?: error("Unexpected first event: $event0")

    (event1 as? AccountNotifications.Event.AssociatedAccount)?.let {
        assertAccountDetailsEqual(it.details, details)
    } ?: error("Unexpected second event: $event1")

    (event2 as? AccountNotifications.Event.DisassociatingAccount)?.let {
        assertAccountDetailsEqual(it.details, details)
    } ?: error("Unexpected third event: $event2")
}
