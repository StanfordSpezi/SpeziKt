package edu.stanford.spezi.account

import edu.stanford.spezi.ui.StringResource

/**
 * Describes a logical category for grouping account keys in UI and validation flows.
 *
 * Categories can be used to organize account information into sections
 * such as credentials, contact details, or personal data.
 *
 * Each category has:
 * - a stable [id] used for identification
 * - an optional localized [title] for display
 */
data class AccountKeyCategory(
    val id: String,
    val title: StringResource?,
) {

    companion object {
        /**
         * Predefined standard categories for common account information types.
         */
        val Name = category(id = "name", stringResId = R.string.account_category_title_name)

        /**
         * The "Credentials" category is intended for account keys related to authentication and security,
         */
        val Credentials = category(id = "credentials", stringResId = R.string.account_category_title_credentials)

        /**
         * The "Contact Details" category is intended for account keys related to contact information,
         * such as email addresses and phone numbers.
         */
        val ContactDetails = category(id = "contact_details", stringResId = R.string.account_category_title_contact_details)

        /**
         * The "Personal Details" category is intended for account keys related to personal information,
         */
        val PersonalDetails = category(id = "personal_details", stringResId = R.string.account_category_title_personal_details)

        /**
         * The "Other" category is a catch-all for account keys that do not fit into the predefined categories.
         */
        val Other = AccountKeyCategory(id = "other", title = null)

        private fun category(id: String, stringResId: Int) = AccountKeyCategory(id = id, title = StringResource.Companion(id = stringResId))
    }
}
