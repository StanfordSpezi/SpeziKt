package edu.stanford.spezi.account.internal.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import edu.stanford.spezi.account.PersonName
import edu.stanford.spezi.resources.Strings
import edu.stanford.spezi.ui.DescriptionGridRow
import edu.stanford.spezi.ui.SpeziInputFieldComposable
import edu.stanford.spezi.ui.account.DataEntryComposable
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

internal class PersonNameDataEntry : DataEntryComposable<PersonName> {
    @Composable
    override fun Content(value: PersonName, onValueChange: (PersonName) -> Unit, modifier: Modifier) {
        Column(modifier = modifier) {
            DescriptionGridRow(
                description = { Text(text = stringResource(Strings.account_name_given_name)) }
            ) {
                SpeziInputFieldComposable(
                    value = value.givenName,
                    placeholder = stringResource(Strings.account_name_given_name_placeholder)
                ) {
                    onValueChange(value.copy(givenName = it))
                }
            }

            DescriptionGridRow(
                description = { Text(text = stringResource(Strings.account_name_family_name)) }
            ) {
                SpeziInputFieldComposable(
                    value = value.familyName,
                    placeholder = stringResource(Strings.account_name_family_name_placeholder)
                ) {
                    onValueChange(value.copy(familyName = it))
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val entry = PersonNameDataEntry()

    SpeziTheme {
        entry.Content(value = PersonName(fullName = "John Doe"), onValueChange = {})
    }
}
