package edu.stanford.spezi.module.account.spezi

import edu.stanford.spezi.module.account.account.AccountNotifications
import edu.stanford.spezi.module.account.account.AccountNotifyConstraint
import edu.stanford.spezi.module.account.account.AccountStorageProvider
import edu.stanford.spezi.module.account.account.Standard
import io.mockk.mockk
import org.junit.Test

class TestStandard : Standard, AccountNotifyConstraint {
    val trackedEvents = mutableListOf<AccountNotifications.Event>()

    override fun respondToEvent(event: AccountNotifications.Event) {
        trackedEvents.add(event)
    }
}

class AccountNotificationsTest {

    private val testProvider: AccountStorageProvider = mockk(relaxed = true)
    private val testStandard = TestStandard()

    @Test
    fun testAccountNotifications() {
        TODO("Empty test")
    }
}
