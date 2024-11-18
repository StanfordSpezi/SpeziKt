package edu.stanford.spezi.core.design.views.personalinfo.fields

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import kotlin.reflect.KMutableProperty1

@Composable
fun NameTextField(
    label: String,
    builder: PersonNameComponents.Builder,
    component: KMutableProperty1<PersonNameComponents.Builder, String?>,
    modifier: Modifier = Modifier,
    prompt: String? = null,
) {
    NameTextField(
        builder = builder,
        component = component,
        modifier = modifier,
        prompt = prompt
    ) {
        Text(label)
    }
}

@Composable
fun NameTextField(
    builder: PersonNameComponents.Builder,
    component: KMutableProperty1<PersonNameComponents.Builder, String?>,
    modifier: Modifier = Modifier,
    prompt: String? = null,
    label: @Composable () -> Unit,
) {
    val textState = remember(builder) {
        mutableStateOf(component.get(builder) ?: "")
    }

    // TODO: Figure out which other options to set on the keyboard for names
    TextField(
        textState.value,
        onValueChange = {
            component.set(builder, it.ifBlank { null })
            textState.value = it
        },
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
        ),
        // TODO: Check if placeholder is the right fit for the prompt property here.
        placeholder = prompt?.let { { Text(it) } },
        label = label,
        modifier = modifier.fillMaxWidth()
    )
}

@ThemePreviews
@Composable
private fun NameTextFieldPreview() {
    val name = remember { PersonNameComponents.Builder() }

    SpeziTheme(isPreview = true) {
        NameTextField(name, PersonNameComponents.Builder::givenName) {
            Text("Enter first name")
        }
    }
}
