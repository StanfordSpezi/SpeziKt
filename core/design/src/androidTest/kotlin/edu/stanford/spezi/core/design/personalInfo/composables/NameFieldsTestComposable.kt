package edu.stanford.spezi.core.design.personalInfo.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.personalInfo.fields.NameFieldRow

@Composable
fun NameFieldsTestComposable() {
    val name = remember { mutableStateOf(PersonNameComponents()) }

    Column {
        NameFieldRow(StringResource("First Name"), name, PersonNameComponents::givenName) {
            Text("enter your first name")
        }

        HorizontalDivider()

        NameFieldRow(StringResource("Last Name"), name, PersonNameComponents::familyName) {
            Text("enter your last name")
        }
    }
}
