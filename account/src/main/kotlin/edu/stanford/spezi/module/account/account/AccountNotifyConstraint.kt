package edu.stanford.spezi.module.account.account

interface AccountNotifyConstraint {
    fun respondToEvent(event: AccountNotifications.Event)
}
