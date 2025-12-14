package edu.stanford.spezi.module.account.account.viewModel

import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.module.account.account.service.configuration.UserIdType
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.email
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.account.value.keys.userIdType

data class AccountDisplayModel(
    val details: AccountDetails,
) {
    val profileViewName: PersonNameComponents? get() = details.name

    val headline: String? get() {
        return details.name?.formatted()
            ?: if (details.contains(AccountKeys.userId)) {
                details.userId
            } else {
                null
            }
    }

    val subHeadline: String? get() {
        if (details.name != null) {
            if (!details.contains(AccountKeys.userId)) {
                return null
            }
            return details.userId
        } else if (details.userIdType != UserIdType.EmailAddress) {
            return details.email
        }
        return null
    }
}
