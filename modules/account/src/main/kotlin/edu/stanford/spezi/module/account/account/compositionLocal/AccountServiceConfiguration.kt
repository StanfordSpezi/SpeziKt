package edu.stanford.spezi.module.account.account.compositionLocal

import androidx.compose.runtime.compositionLocalOf
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.SupportedAccountKeys

val LocalAccountServiceConfiguration = compositionLocalOf {
    AccountServiceConfiguration(SupportedAccountKeys.Arbitrary)
}
