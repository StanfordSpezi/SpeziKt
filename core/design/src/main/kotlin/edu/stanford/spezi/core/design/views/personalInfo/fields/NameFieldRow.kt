package edu.stanford.spezi.core.design.views.personalInfo.fields

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.views.layout.DescriptionGridRow
import kotlin.reflect.KMutableProperty1

@Composable
fun NameFieldRow(
    description: StringResource,
    name: MutableState<PersonNameComponents>,
    component: KMutableProperty1<PersonNameComponents, String?>,
    label: @Composable () -> Unit,
) {
    NameFieldRow(
        name = name,
        component = component,
        description = { Text(description.text()) },
        label = label
    )
}

@Composable
fun NameFieldRow(
    name: MutableState<PersonNameComponents>,
    component: KMutableProperty1<PersonNameComponents, String?>,
    description: @Composable () -> Unit,
    label: @Composable () -> Unit,
) {
    DescriptionGridRow(
        description = description,
        content = {
            NameTextField(name, component) {
                label()
            }
        }
    )
}

@ThemePreviews
@Composable
private fun NameFieldRowPreview() {
    val name = remember { mutableStateOf(PersonNameComponents()) }

    Column {
        NameFieldRow(
            name,
            PersonNameComponents::givenName,
            description = { Text("First") }
        ) {
            Text("enter first name")
        }

        HorizontalDivider(Modifier.padding(vertical = 15.dp))

        // Last Name Field
        NameFieldRow(
            name,
            PersonNameComponents::familyName,
            description = { Text("Last") }
        ) {
            Text("enter last name")
        }
    }
}
