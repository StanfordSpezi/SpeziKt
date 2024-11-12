package edu.stanford.spezi.module.account.account.value

import edu.stanford.spezi.core.design.component.StringResource

data class AccountKeyCategory internal constructor(
    val categoryTitle: StringResource? = null,
) {
    companion object {
        val credentials = AccountKeyCategory(StringResource("UP_CREDENTIALS"))
        val name = AccountKeyCategory(StringResource("UP_NAME"))
        val contactDetails = AccountKeyCategory(StringResource("UP_CONTACT_DETAILS"))
        val personalDetails = AccountKeyCategory(StringResource("UP_PERSONAL_DETAILS"))
        val other = AccountKeyCategory()

        operator fun invoke(title: StringResource): AccountKeyCategory {
            return AccountKeyCategory(categoryTitle = title)
        }
    }
}
