package edu.stanford.spezi.module.account.account.value.keys

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccountViewType
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyRequirement
import edu.stanford.spezi.module.account.account.views.entry.DateOfBirthPicker
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.DateFormat
import java.time.Instant
import java.util.Date

private object AccountDateOfBirthKey : AccountKey<Date> {
    override val identifier = "dateOfBirth"
    override val name = StringResource("UAP_SIGNUP_DATE_OF_BIRTH_TITLE")
    override val category = AccountKeyCategory.personalDetails
    override val initialValue: InitialValue<Date> = InitialValue.Empty(Date())
    override val serializer = object : KSerializer<Date> {
        override val descriptor: SerialDescriptor
            get() = String.serializer().descriptor

        override fun serialize(encoder: Encoder, value: Date) {
            encoder.encodeString(value.toInstant().toString())
        }

        override fun deserialize(decoder: Decoder): Date {
            return Date.from(Instant.parse(decoder.decodeString()))
        }
    }

    @Composable
    override fun DisplayComposable(value: Date) {
        val format = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }
        ListRow(AccountKeys.dateOfBirth.name.text()) {
            Text(format.format(value))
        }
    }

    @Composable
    override fun EntryComposable(value: Date, onValueChanged: (Date) -> Unit) {
        val account = LocalAccount.current
        val accountViewType = LocalAccountViewType.current

        val isRequired =
            account?.configuration?.get(this)?.requirement == AccountKeyRequirement.REQUIRED ||
                accountViewType?.isEnteringNewData == false

        DateOfBirthPicker(name.text(), value, onValueChanged, isRequired)
    }
}

val AccountKeys.dateOfBirth: AccountKey<Date>
    get() = AccountDateOfBirthKey

var AccountDetails.dateOfBirth: Date?
    get() = this.storage[AccountKeys.dateOfBirth]
    set(value) { this.storage[AccountKeys.dateOfBirth] = value }
