package edu.stanford.spezi.module.account.account.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.personalInfo.UserProfileComposable
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.account.viewModel.AccountDisplayModel

@Composable
internal fun AccountSummaryBox(details: AccountDetails) {
    val model = AccountDisplayModel(details)
    Row {
        model.profileViewName?.let {
            UserProfileComposable(
                name = it,
                modifier = Modifier.height(40.dp)
            )
        } ?: run {
            /* iOS:
                    Image(systemName: "person.crop.circle.fill")
                        .resizable()
                        .frame(width: 40, height: 40)
                        #if os(macOS)
                        .foregroundColor(Color(.systemGray))
                        #else
                        .foregroundColor(Color(uiColor: .systemGray3))
                        #endif
                        .accessibilityHidden(true)
             */
        }

        Column {
            Text(model.headline ?: StringResource("Anonymous User").text())
            model.subHeadline?.let {
                Text(it) // TODO: subheadline font, foreground color .secondary
            }
        }
    }
}

class AccountDetailsProvider : PreviewParameterProvider<AccountDetails> {
    override val values: Sequence<AccountDetails> = sequenceOf(
        AccountDetails().also {
            it.userId = "lelandstanford@stanford.edu"
            it.name = PersonNameComponents(givenName = "Leland", familyName = "Stanford")
        },
        AccountDetails().also {
            it.userId = "leland.stanford"
            it.name = PersonNameComponents(givenName = "Leland", familyName = "Stanford")
        },
        AccountDetails().also {
            it.userId = "leland.stanford"
        },
        AccountDetails().also {
            it.userId = "lelandstanford@stanford.edu"
        },
    )
}

@ThemePreviews
@Composable
fun AccountDialogPreview(
    @PreviewParameter(AccountDetailsProvider::class) details: AccountDetails,
) {
    SpeziTheme(isPreview = true) {
        AccountSummaryBox(
            details = details
        )
    }
}
