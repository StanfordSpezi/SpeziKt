package edu.stanford.spezi.module.account.account.value.keys

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.personalinfo.fields.NameFieldRow
import edu.stanford.spezi.core.design.views.validation.Validate
import edu.stanford.spezi.core.design.views.validation.ValidationEngine
import edu.stanford.spezi.core.design.views.validation.ValidationRule
import edu.stanford.spezi.core.design.views.validation.configuration.LocalValidationEngineConfiguration
import edu.stanford.spezi.core.design.views.validation.nonEmpty
import edu.stanford.spezi.core.design.views.validation.state.ReceiveValidation
import edu.stanford.spezi.core.design.views.validation.state.ValidationContext
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.model.acceptAll
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.configuration.AccountKeyRequirement
import edu.stanford.spezi.module.account.account.views.display.GridValidationStateFooter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.EnumSet

private object AccountNameKey : AccountKey<PersonNameComponents> {
    override val identifier = "name"
    override val name = StringResource("NAME")
    override val category = AccountKeyCategory.credentials
    override val initialValue: InitialValue<PersonNameComponents> = InitialValue.Empty(
        PersonNameComponents())
    override val serializer = object : KSerializer<PersonNameComponents> {
        override val descriptor = String.serializer().descriptor

        override fun serialize(encoder: Encoder, value: PersonNameComponents) {
            encoder.encodeString(value.formatted())
        }

        override fun deserialize(decoder: Decoder): PersonNameComponents {
            return PersonNameComponents(decoder.decodeString())
        }
    }

    @Composable
    override fun Display(value: PersonNameComponents) {
        ListRow(name.text()) {
            Text(value.formatted())
        }
    }

    @Composable
    override fun Entry(value: PersonNameComponents, onValueChanged: (PersonNameComponents) -> Unit) {
        val account = LocalAccount.current
        val nameIsRequired = account?.configuration?.get(AccountKeys.name)?.requirement == AccountKeyRequirement.REQUIRED
        val validationRules = if (nameIsRequired) listOf(ValidationRule.nonEmpty) else listOf(ValidationRule.acceptAll)

        val givenNameValidation = remember { mutableStateOf(ValidationContext()) }
        val familyNameValidation = remember { mutableStateOf(ValidationContext()) }

        val validationConfiguration = remember {
            EnumSet.of(ValidationEngine.ConfigurationOption.CONSIDER_NO_INPUT_AS_VALID)
        }

        CompositionLocalProvider(LocalValidationEngineConfiguration provides validationConfiguration) {
            Column {
                ReceiveValidation(givenNameValidation) {
                    Validate(value.givenName ?: "", rules = validationRules) {
                        NameFieldRow(
                            value,
                            PersonNameComponents::givenName,
                            onValueChanged = {
                                onValueChanged(
                                    value.copy(givenName = it)
                                )
                            },
                            description = {
                                Text(StringResource("UAP_SIGNUP_GIVEN_NAME_TITLE").text())
                            },
                        ) {
                            Text(StringResource("UAP_SIGNUP_GIVEN_NAME_PLACEHOLDER").text())
                        }
                    }
                }

                GridValidationStateFooter(givenNameValidation.value.allDisplayedValidationResults)

                HorizontalDivider()

                ReceiveValidation(familyNameValidation) {
                    Validate(value.familyName ?: "", rules = validationRules) {
                        NameFieldRow(
                            value,
                            PersonNameComponents::familyName,
                            onValueChanged = {
                                onValueChanged(
                                    value.copy(familyName = it)
                                )
                            },
                            description = {
                                Text(StringResource("UAP_SIGNUP_FAMILY_NAME_TITLE").text())
                            },
                        ) {
                            Text(StringResource("UAP_SIGNUP_FAMILY_NAME_PLACEHOLDER").text())
                        }
                    }
                }

                GridValidationStateFooter(familyNameValidation.value.allDisplayedValidationResults)
            }
        }
    }
}

val AccountKeys.name: AccountKey<PersonNameComponents>
    get() = AccountNameKey

var AccountDetails.name: PersonNameComponents?
    get() = this.storage[AccountKeys.name]
    set(value) { this.storage[AccountKeys.name] = value }
