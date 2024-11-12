package edu.stanford.spezi.module.account.account.value.keys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.model.GenderIdentity
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.value
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

private object AccountGenderIdentityKey : AccountKey<GenderIdentity> {
    override val identifier = "genderIdentity"
    override val name = StringResource("GENDER_IDENTITY_TITLE")
    override val category = AccountKeyCategory.personalDetails
    override val initialValue: InitialValue<GenderIdentity> = InitialValue.Default(GenderIdentity.PREFER_NOT_TO_STATE)
    override val serializer = object : KSerializer<GenderIdentity> {
        override val descriptor = String.serializer().descriptor

        override fun serialize(encoder: Encoder, value: GenderIdentity) {
            encoder.encodeString(value.name)
        }

        override fun deserialize(decoder: Decoder): GenderIdentity {
            val string = decoder.decodeString()
            return GenderIdentity.entries.first { it.name == string }
        }
    }

    @Composable
    override fun DisplayComposable(value: GenderIdentity) {
        TODO("Not yet implemented")
    }

    @Composable
    override fun EntryComposable(state: MutableState<GenderIdentity>) {
        TODO("Not yet implemented")
    }
}

val AccountKeys.genderIdentity: AccountKey<GenderIdentity>
    get() = AccountGenderIdentityKey

var AccountDetails.genderIdentity: GenderIdentity?
    get() = this.storage[AccountKeys.genderIdentity]
    set(value) { this.storage[AccountKeys.genderIdentity] = value }
