package edu.stanford.spezi.ui.personalinfo

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.ui.DescriptionGridRow
import edu.stanford.spezi.ui.SpeziTheme
import edu.stanford.spezi.ui.ThemePreviews
import kotlin.reflect.KMutableProperty1

@Composable
fun NameFieldRow(
    description: String,
    builder: PersonNameComponents.Builder,
    property: KMutableProperty1<PersonNameComponents.Builder, String?>,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
) {
    NameFieldRow(
        builder = builder,
        property = property,
        description = { Text(description) },
        modifier = modifier,
        label = label
    )
}

@Composable
fun NameFieldRow(
    builder: PersonNameComponents.Builder,
    property: KMutableProperty1<PersonNameComponents.Builder, String?>,
    description: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
) {
    DescriptionGridRow(
        description = description,
        modifier = modifier,
        content = {
            NameTextField(
                builder = builder,
                property = property,
                label = label,
            )
        }
    )
}

@ThemePreviews
@Composable
private fun NameFieldRowPreview() {
    val nameBuilder = remember { PersonNameComponents.Builder() }

    SpeziTheme(isPreview = true) {
        Column {
            NameFieldRow(
                nameBuilder,
                PersonNameComponents.Builder::givenName,
                description = { Text("First") }
            ) {
                Text("enter first name")
            }

            HorizontalDivider(Modifier.padding(vertical = 15.dp))

            // Last Name Field
            NameFieldRow(
                nameBuilder,
                PersonNameComponents.Builder::familyName,
                description = { Text("Last") }
            ) {
                Text("enter last name")
            }
        }
    }
}
