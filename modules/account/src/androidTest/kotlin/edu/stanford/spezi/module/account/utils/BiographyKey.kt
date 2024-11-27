package edu.stanford.spezi.module.account.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.validation.views.VerifiableTextField
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import kotlinx.serialization.builtins.serializer

private object BiographyKey : AccountKey<String> {
    override val identifier = "biography"
    override val name = StringResource("Bio")
    override val initialValue: InitialValue<String>
        get() = InitialValue.Empty("")
    override val serializer get() = String.serializer()

    @Composable
    override fun Display(value: String) {
        ListRow(name.text()) {
            Text(value)
        }
    }

    @Composable
    override fun Entry(value: String, onValueChanged: (String) -> Unit) {
        VerifiableTextField(
            name.text(),
            value = value,
            onValueChanged = onValueChanged,
            disableAutocorrection = true,
        )
    }
}

var AccountDetails.biography: String?
    get() = this[BiographyKey]
    set(value) { this[BiographyKey] = value }
