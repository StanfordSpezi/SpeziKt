package edu.stanford.spezi.module.account.account.compositionLocal

import edu.stanford.spezi.module.account.account.value.AccountKey
import java.util.Date

data class SignUpProviderCompliance internal constructor(
    private val creationDate: Date = Date(),
    val visualizedAccountKeys: VisualizedAccountKeys
) {
    sealed interface VisualizedAccountKeys {
        data object All : VisualizedAccountKeys
        data class Only(val keys: List<AccountKey<*>>) : VisualizedAccountKeys
    }

    companion object {
        val compliant: SignUpProviderCompliance
            get() = SignUpProviderCompliance(visualizedAccountKeys = VisualizedAccountKeys.All)

        fun askedFor(keys: List<AccountKey<*>>) =
            SignUpProviderCompliance(visualizedAccountKeys = VisualizedAccountKeys.Only(keys))
    }
}

// TODO: Find equivalent to SwiftUI preferences and implement inject functionality
