package edu.stanford.spezi.module.account.utils

import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.views.display.StringDisplay
import edu.stanford.spezi.module.account.account.views.entry.StringEntry
import kotlinx.serialization.builtins.serializer

private object InvitationCodeKey : AccountKey<String> {
    override val identifier = "invitationCode"
    override val name = StringResource("Invitation Code")
    override val initialValue: InitialValue<String>
        get() = InitialValue.Empty("")
    override val serializer get() = String.serializer()

    @Composable
    override fun Display(value: String) {
        StringDisplay(this, value)
    }

    @Composable
    override fun Entry(value: String, onValueChanged: (String) -> Unit) {
        StringEntry(this, value, onValueChanged)
    }
}

val AccountKeys.invitationCode: AccountKey<String>
    get() = InvitationCodeKey

var AccountDetails.invitationCode: String?
    get() = this[AccountKeys.invitationCode]
    set(value) { this[AccountKeys.invitationCode] = value }
