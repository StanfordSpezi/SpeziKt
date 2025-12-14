package edu.stanford.spezi.module.account.utils

import androidx.compose.runtime.mutableStateOf
import edu.stanford.spezi.module.account.account.AccountNotifications
import edu.stanford.spezi.module.account.account.AccountNotifyConstraint
import edu.stanford.spezi.module.account.account.Standard
import javax.inject.Inject

class TestStandard @Inject constructor() : Standard, AccountNotifyConstraint {
    private val _deleteNotified = mutableStateOf(false)
    val deleteNotified get() = _deleteNotified.value

    override fun respondToEvent(event: AccountNotifications.Event) {
        if (event is AccountNotifications.Event.DeletingAccount) {
            _deleteNotified.value = true
        }
    }
}
