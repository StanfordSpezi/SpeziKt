package edu.stanford.spezi.module.account.manager

data class UserState(
    val isAnonymous: Boolean,
    val hasConsented: Boolean,
)
