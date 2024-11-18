package edu.stanford.spezi.core.design.personalInfo.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.personalinfo.fields.NameFieldRow

@Composable
fun NameFieldsTestComposable() {
    val nameBuilder = remember { PersonNameComponents.Builder() }

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
