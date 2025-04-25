package edu.stanford.spezi.ui.personalinfo

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.ThemePreviews
import edu.stanford.spezi.ui.testing.testIdentifier
import kotlin.reflect.KMutableProperty1

enum class NameTextFieldTestIdentifier {
    TEXT_FIELD,
}

@Composable
fun NameTextField(
    label: String,
    builder: PersonNameComponents.Builder,
    property: KMutableProperty1<PersonNameComponents.Builder, String?>,
    modifier: Modifier = Modifier,
) {
    NameTextField(
        builder = builder,
        property = property,
        modifier = modifier,
    ) {
        Text(label)
    }
}

// TODO: We got rid of "prompt" property here
@Composable
fun NameTextField(
    builder: PersonNameComponents.Builder,
    property: KMutableProperty1<PersonNameComponents.Builder, String?>,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
) {
    val textState = remember(builder) {
        mutableStateOf(property.get(builder) ?: "")
    }

    // TODO: Figure out which other options to set on the keyboard for names
    TextField(
        textState.value,
        onValueChange = {
            property.set(builder, it.ifBlank { null })
            textState.value = it
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
    val name = remember { PersonNameComponents.Builder() }

    SpeziTheme {
        NameTextField(name, PersonNameComponents.Builder::givenName) {
            Text("Enter first name")
        }
    }
}
