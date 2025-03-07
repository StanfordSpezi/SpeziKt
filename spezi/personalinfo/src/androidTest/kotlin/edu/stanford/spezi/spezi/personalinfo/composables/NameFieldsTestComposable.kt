package edu.stanford.spezi.spezi.personalinfo.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.spezi.personalinfo.PersonNameComponents
import edu.stanford.spezi.spezi.personalinfo.fields.NameFieldRow

@Composable
fun NameFieldsTestComposable(nameBuilder: PersonNameComponents.Builder) {
    Column {
        NameFieldRow("First Name", nameBuilder, PersonNameComponents.Builder::givenName) {
            Text("enter your first name")
        }

        HorizontalDivider()

        NameFieldRow("Last Name", nameBuilder, PersonNameComponents.Builder::familyName) {
            Text("enter your last name")
        }
    }
}
