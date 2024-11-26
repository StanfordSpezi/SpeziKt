package edu.stanford.spezi.module.account.account.value.keys

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.PasswordVisualTransformation
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.validation.configuration.LocalValidationEngine
import edu.stanford.spezi.core.design.views.validation.views.TextFieldType
import edu.stanford.spezi.core.design.views.validation.views.VerifiableTextField
import edu.stanford.spezi.core.design.views.views.layout.DescriptionGridRow
import edu.stanford.spezi.module.account.account.compositionLocal.AccountViewType
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccountViewType
import edu.stanford.spezi.module.account.account.compositionLocal.LocalPasswordFieldType
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.views.display.GridValidationStateFooter
import edu.stanford.spezi.module.account.account.views.display.StringDisplay
import kotlinx.serialization.builtins.serializer

private object AccountPasswordKey : AccountKey<String> {
    override val identifier = "password"
    override val name = StringResource("UP_PASSWORD")
    override val category = AccountKeyCategory.credentials
    override val initialValue: InitialValue<String> = InitialValue.Empty("")

    // TODO: Since password is not supposed to be serialized,
    //  we could also just throw an error when this is attempted
    //  or we ignore the serialization step
    override val serializer = String.serializer()

    @Composable
    override fun Display(value: String) {
        StringDisplay(this, value)
    }

    @Composable
    override fun Entry(value: String, onValueChanged: (String) -> Unit) {
        val accountViewType = LocalAccountViewType.current
        val fieldType = LocalPasswordFieldType.current
        val validation = LocalValidationEngine.current

        when (accountViewType) {
            null, AccountViewType.Signup -> {
                VerifiableTextField(fieldType.stringResource.text(), value, onValueChanged, type = TextFieldType.SECURE)
                // TODO: TextContentType, Disable field assistants
            }
            is AccountViewType.Overview -> {
                DescriptionGridRow(
                    description = {
                        Text(fieldType.stringResource.text())
                    }
                ) {
                    TextField(
                        value,
                        onValueChange = onValueChanged,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    // TODO: TextContentType, Disable field assistants
                }

                GridValidationStateFooter(validation?.displayedValidationResults ?: emptyList())
            }
        }
    }
}

val AccountKeys.password: AccountKey<String>
    get() = AccountPasswordKey

var AccountDetails.password: String?
    get() = this.storage[AccountKeys.password]
    set(value) { this.storage[AccountKeys.password] = value }
