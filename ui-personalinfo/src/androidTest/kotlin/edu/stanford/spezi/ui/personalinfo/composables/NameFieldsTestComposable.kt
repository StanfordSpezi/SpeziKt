package edu.stanford.spezi.ui.personalinfo.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import edu.stanford.spezi.ui.personalinfo.NameFieldRow
import edu.stanford.spezi.ui.personalinfo.PersonNameComponents

@Composable
fun NameFieldsTestComposable(nameBuilder: PersonNameComponents.Builder) {
    Column {
        NameFieldRow(
            builder = nameBuilder,
            property = PersonNameComponents.Builder::givenName,
            description = {
                "First Name"
            },
            placeholder = {
                Text("enter your first name")
            },
        )

        HorizontalDivider()

        NameFieldRow(
            builder = nameBuilder,
            property = PersonNameComponents.Builder::familyName,
            description = {
                "Last Name"
            },
            placeholder = {
                Text("enter your last name")
            },
        )
    }
}
