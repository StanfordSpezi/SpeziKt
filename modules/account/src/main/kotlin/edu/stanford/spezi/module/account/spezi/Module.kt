package edu.stanford.spezi.module.account.spezi

import androidx.annotation.MainThread

interface Module {
    @MainThread
    fun configure() {}
}
