package edu.stanford.spezi.module.account.account.compositionLocal

import androidx.compose.runtime.compositionLocalOf
import edu.stanford.spezi.module.account.account.Account

val LocalAccount = compositionLocalOf<Account?> { null }
