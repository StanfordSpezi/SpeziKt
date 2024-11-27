package edu.stanford.spezi.module.account.utils

import edu.stanford.spezi.module.account.account.AccountNotifications
import edu.stanford.spezi.module.account.account.AccountNotifyConstraint
import edu.stanford.spezi.module.account.account.Standard
import javax.inject.Inject

class TestStandard @Inject constructor() : Standard, AccountNotifyConstraint {
    var deleteNotified = false

    override fun respondToEvent(event: AccountNotifications.Event) {
        if (event is AccountNotifications.Event.DeletingAccount) {
            deleteNotified = true
        }
    }
}
