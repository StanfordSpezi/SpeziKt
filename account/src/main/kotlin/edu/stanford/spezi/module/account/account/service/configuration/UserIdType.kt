package edu.stanford.spezi.module.account.account.service.configuration

import edu.stanford.spezi.core.design.component.StringResource

sealed class UserIdType(val stringResource: StringResource) {
    data object EmailAddress : UserIdType(StringResource("USER_ID_EMAIL"))
    data object Username : UserIdType(StringResource("USER_ID_USERNAME"))
    class Other(stringResource: StringResource) : UserIdType(stringResource)
}
