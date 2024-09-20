package edu.stanford.spezi.module.account.account

typealias LocalizedStringResource = String

data class AccountKeyCategory(val title: LocalizedStringResource) {
    companion object {
        val credentials = AccountKeyCategory("Credentials")
        val name = AccountKeyCategory("Name")
        val contactDetails = AccountKeyCategory("Contact Details")
        val personalDetails = AccountKeyCategory("Personal Details")
        val other = AccountKeyCategory("")
    }
}