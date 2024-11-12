package edu.stanford.spezi.module.account.account.compositionLocal

import androidx.compose.runtime.compositionLocalOf
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.keys.password

enum class PasswordFieldType {
    PASSWORD, NEW, REPEAT;

    val text: StringResource get() = when (this) {
        PASSWORD -> AccountKeys.password.name
        NEW -> StringResource("NEW_PASSWORD")
        REPEAT -> StringResource("REPEAT_PASSWORD")
    }

    val prompt: StringResource get() = when (this) {
        PASSWORD -> AccountKeys.password.name
        NEW -> StringResource("NEW_PASSWORD_PROMPT")
        REPEAT -> StringResource("REPEAT_PASSWORD_PROMPT")
    }
}

val LocalPasswordFieldType = compositionLocalOf { PasswordFieldType.PASSWORD }
