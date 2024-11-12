package edu.stanford.spezi.core.design.views.personalInfo.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents
import kotlin.reflect.KMutableProperty1

@Composable
fun NameTextField(
    label: StringResource,
    name: MutableState<PersonNameComponents>,
    component: KMutableProperty1<PersonNameComponents, String?>,
    modifier: Modifier = Modifier,
    prompt: StringResource? = null,
) {
    NameTextField(name, component, modifier, prompt) {
        Text(label.text())
    }
}

@Composable
fun NameTextField(
    name: MutableState<PersonNameComponents>,
    component: KMutableProperty1<PersonNameComponents, String?>,
    modifier: Modifier = Modifier,
    prompt: StringResource? = null,
    label: @Composable () -> Unit,
) {
    // TODO: Figure out which other options to set on the keyboard for names
    TextField(
        component.get(name.value) ?: "",
        onValueChange = {
            if (it.isBlank()) {
                component.set(name.value, null)
            } else {
                component.set(name.value, it)
            }
        },
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
        ),
        // TODO: Check if placeholder is the right fit for the prompt property here.
        placeholder = prompt?.let { { Text(it.text()) } },
        label = label,
        modifier = modifier.fillMaxWidth()
    )
}

@ThemePreviews
@Composable
private fun NameTextFieldPreview() {
    val name = remember { mutableStateOf(PersonNameComponents()) }

    SpeziTheme(isPreview = true) {
        NameTextField(name, PersonNameComponents::givenName) {
            Text("Enter first name")
        }
    }
}
