package edu.stanford.spezi.module.account.account.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.component.ImageResourceComposable
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.personalinfo.UserProfileComposable
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.userId
import edu.stanford.spezi.module.account.account.viewModel.AccountDisplayModel

@Composable
internal fun AccountSummaryBox(
    details: AccountDetails,
    modifier: Modifier = Modifier,
) {
    val model = AccountDisplayModel(details)
    Box(
        modifier
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(8.dp),
                spotColor = Colors.primary
            )
            .padding(8.dp)
    ) {
        Row(Modifier.padding(8.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            model.profileViewName?.let {
                UserProfileComposable(
                    name = it,
                    modifier = Modifier.height(40.dp)
                )
            } ?: ImageResourceComposable(
                ImageResource.Vector(Icons.Default.Person, StringResource("User Profile")),
                modifier = Modifier.size(40.dp),
                tint = Colors.secondary,
            )

            Column(Modifier.padding(start = 16.dp)) {
                Text(
                    model.headline ?: StringResource("Anonymous User").text(),
                    style = TextStyles.bodyLarge
                )
                model.subHeadline?.let {
                    Text(
                        it,
                        style = TextStyles.bodyMedium,
                        color = Colors.secondary,
                    )
                }
            }
        }
    }
}

private class AccountSummaryBoxPreviewProvider : PreviewParameterProvider<AccountDetails> {
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
private fun AccountSummaryBoxPreviews(
    @PreviewParameter(AccountSummaryBoxPreviewProvider::class) details: AccountDetails,
) {
    SpeziTheme(isPreview = true) {
        Box(Modifier.padding(8.dp)) {
            AccountSummaryBox(
                details = details
            )
        }
    }
}
