package edu.stanford.spezi.module.account.account.views.overview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
internal fun AccountOverviewHeader(
    details: AccountDetails,
    modifier: Modifier = Modifier,
) {
    val model = AccountDisplayModel(details)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        model.profileViewName?.let {
            UserProfileComposable(
                name = it,
                modifier = Modifier.height(90.dp)
            )
        } ?: Image(
            Icons.Outlined.AccountCircle,
            null,
            Modifier.size(40.dp, 40.dp)
        )

        model.headline?.let {
            Text(it, style = TextStyles.headlineMedium, fontWeight = FontWeight.SemiBold)
        }

        model.subHeadline?.let {
            Text(it, style = TextStyles.titleMedium, color = Colors.secondary)
        }
    }
}

@ThemePreviews
@Composable
private fun AccountOverviewHeaderPreview() {
    SpeziTheme(isPreview = true) {
        val details = AccountDetails()
        details.userId = "lelandstanford@stanford.edu"
        details.name = PersonNameComponents(givenName = "Leland", familyName = "Stanford")
        AccountOverviewHeader(details)
    }
}
