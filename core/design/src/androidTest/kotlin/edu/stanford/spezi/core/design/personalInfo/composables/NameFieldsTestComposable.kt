package edu.stanford.spezi.core.design.personalInfo.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.personalinfo.fields.NameFieldRow

@Composable
fun NameFieldsTestComposable(name: MutableState<PersonNameComponents>) {
    Column {
        NameFieldRow(
            "First Name",
            name.value,
            PersonNameComponents::givenName,
            onValueChanged = { name.value = name.value.copy(givenName = it) }
        ) {
            Text("enter your first name")
        }

        HorizontalDivider()

        NameFieldRow(
            "Last Name",
            name.value,
            PersonNameComponents::familyName,
            onValueChanged = { name.value = name.value.copy(familyName = it) }
        ) {
            Text("enter your last name")
        }
    }
}
