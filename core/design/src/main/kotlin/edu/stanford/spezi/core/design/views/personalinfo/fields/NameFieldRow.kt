package edu.stanford.spezi.core.design.views.personalinfo.fields

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.views.layout.DescriptionGridRow
import kotlin.reflect.KProperty1

@Composable
fun NameFieldRow(
    description: String,
    name: PersonNameComponents,
    property: KProperty1<PersonNameComponents, String?>,
    onValueChanged: (String?) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
) {
    NameFieldRow(
        name = name,
        property = property,
        onValueChanged = onValueChanged,
        description = { Text(description) },
        modifier = modifier,
        label = label
    )
}

@Composable
fun NameFieldRow(
    name: PersonNameComponents,
    property: KProperty1<PersonNameComponents, String?>,
    onValueChanged: (String?) -> Unit,
    description: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
) {
    DescriptionGridRow(
        description = description,
        modifier = modifier,
        content = {
            NameTextField(
                name = name,
                property = property,
                onValueChanged = onValueChanged,
                label = label,
            )
        }
    )
}

@ThemePreviews
@Composable
private fun NameFieldRowPreview() {
    var name by remember { mutableStateOf(PersonNameComponents()) }

    SpeziTheme(isPreview = true) {
        Column {
            NameFieldRow(
                name,
                PersonNameComponents::givenName,
                onValueChanged = {
                    name = name.copy(givenName = it)
                },
                description = { Text("First") }
            ) {
                Text("enter first name")
            }

            HorizontalDivider(Modifier.padding(vertical = 15.dp))

            // Last Name Field
            NameFieldRow(
                name,
                PersonNameComponents::familyName,
                onValueChanged = {
                    name = name.copy(familyName = it)
                },
                description = { Text("Last") }
            ) {
                Text("enter last name")
            }
        }
    }
}
