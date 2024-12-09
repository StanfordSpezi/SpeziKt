package edu.stanford.spezi.core.design.views.personalinfo.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import kotlin.reflect.KProperty1

enum class NameTextFieldTestIdentifier {
    TEXT_FIELD,
}

@Composable
fun NameTextField(
    label: String,
    name: PersonNameComponents,
    property: KProperty1<PersonNameComponents, String?>,
    onValueChanged: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    NameTextField(
        name = name,
        property = property,
        onValueChanged = onValueChanged,
        modifier = modifier,
    ) {
        Text(label)
    }
}

// TODO: We got rid of "prompt" property here
@Composable
fun NameTextField(
    name: PersonNameComponents,
    property: KProperty1<PersonNameComponents, String?>,
    onValueChanged: (String?) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
) {
    // TODO: Figure out which other options to set on the keyboard for names
    TextField(
        property.get(name) ?: "",
        onValueChange = {
            onValueChanged(it.ifBlank { null })
        },
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
        ),
        placeholder = label,
        modifier = modifier
            .fillMaxWidth()
            .testIdentifier(NameTextFieldTestIdentifier.TEXT_FIELD, property.name)
    )
}

@ThemePreviews
@Composable
private fun NameTextFieldPreview() {
    var name by remember { mutableStateOf(PersonNameComponents()) }

    SpeziTheme(isPreview = true) {
        NameTextField(
            name,
            PersonNameComponents::givenName,
            { name = name.copy(givenName = it) },
        ) {
            Text("Enter first name")
        }
    }
}
