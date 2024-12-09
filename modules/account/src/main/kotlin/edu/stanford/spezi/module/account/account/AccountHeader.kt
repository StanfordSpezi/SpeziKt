package edu.stanford.spezi.module.account.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.component.ImageResourceComposable
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.personalinfo.UserProfileComposable
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.userId

@Composable
fun AccountHeader(
    caption: String = StringResource("ACCOUNT_HEADER_CAPTION").text(),
) {
    AccountHeader(
        caption = caption,
        accountDetails = LocalAccount.current?.details
    )
}

@Composable
private fun AccountHeader(
    caption: String,
    accountDetails: AccountDetails?,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacings.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        accountDetails?.name?.let { name ->
            UserProfileComposable(
                name = name,
                modifier = Modifier.height(60.dp)
            )
        } ?: ImageResourceComposable(
            ImageResource.Vector(Icons.Default.Person, StringResource("Person")),
            modifier = Modifier.size(60.dp),
            tint = Colors.secondary,
        )

        Column {
            val name = accountDetails?.name?.formatted()
                ?: accountDetails?.userId

            name?.let {
                Text(
                    name,
                    style = TextStyles.headlineSmall,
                )
            } ?: Box(
                modifier = Modifier
                    .padding(bottom = Spacings.extraSmall)
                    .background(
                        color = Colors.primary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp) // Corner radius
                    )
            ) {
                Text("Placeholder", modifier = Modifier.alpha(0f))
            }

            Text(
                caption,
                style = TextStyles.labelSmall
            )
        }
    }
}

private class AccountHeaderPreviewProvider :
    PreviewParameterProvider<AccountDetails?> {
    override val values: Sequence<AccountDetails?> = sequenceOf(
        AccountDetails().apply {
            userId = "lelandstanford@stanford.edu"
            name = PersonNameComponents(givenName = "Leland", familyName = "Stanford")
        },
        null,
        AccountDetails().apply {
            userId = "lelandstanford@stanford.edu"
        }
    )
}

@ThemePreviews
@Composable
private fun AccountHeaderPreview(
    @PreviewParameter(AccountHeaderPreviewProvider::class) details: AccountDetails?,
) {
    SpeziTheme(isPreview = true) {
        AccountHeader(
            caption = "Account Information & Details",
            accountDetails = details
        )
    }
}
