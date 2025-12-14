package edu.stanford.spezi.ui.personalinfo.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.ui.personalinfo.NameFieldRow
import edu.stanford.spezi.ui.personalinfo.OutlinedNameFieldRow
import edu.stanford.spezi.ui.personalinfo.PersonNameComponents

@Composable
fun NameFieldsTestComposable(nameBuilder: PersonNameComponents.Builder) {
    Column {
        NameFieldRow(
            builder = nameBuilder,
            property = PersonNameComponents.Builder::givenName,
            description = {
                Text("First Name")
            },
            placeholder = {
                Text("enter your first name")
            },
        )

        HorizontalDivider()

        OutlinedNameFieldRow(
            builder = nameBuilder,
            property = PersonNameComponents.Builder::familyName,
            description = {
                Text("Last Name")
            },
            placeholder = {
                Text("enter your last name")
            },
        )
    }
}
