package edu.stanford.spezi.module.account.account.modifier

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccountRequired
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.isAnonymous

@Composable
fun AccountRequired(
    enabled: Boolean = true,
    accountSetupIsComplete: (AccountDetails) -> Boolean = { !it.isAnonymous },
    setupSheet: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val account = LocalAccount.current

    val shouldPresentSheet: Boolean = if (!enabled || account == null) {
        false
    } else {
        account.details?.let(accountSetupIsComplete) ?: true
    }

    CompositionLocalProvider(LocalAccountRequired provides enabled) {
        // TODO: Should we wrap this in a ModalBottomSheet or sth?
        //  Especially since we may want to disable interactive dismiss etc
        if (shouldPresentSheet) setupSheet()

        content()
    }
}
