package edu.stanford.spezi.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziShapes
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews
import edu.stanford.spezi.ui.theme.bold

/**
 * Composable content rendering the header of the account overview screen.
 *
 * @param initials Optional initials of the user.
 * @param name Name of the user.
 * @param email Email of the user.
 */
data class AccountProfileHeader(
    val initials: String?,
    val name: String,
    val email: String,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Spacings.small),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            initials?.let {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(Colors.surface, SpeziShapes.circle),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = it,
                        style = TextStyles.headlineLarge.bold(),
                    )
                }
            }

            Text(
                text = name,
                style = TextStyles.titleLarge.bold(),
            )

            Text(
                text = email,
                style = TextStyles.bodyMedium,
                color = Colors.secondary,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val header = AccountProfileHeader(
        initials = "LS",
        name = "Leland Stanford",
        email = "lelandstanford@stanford.edu"
    )

    SpeziTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            header.Content()
        }
    }
}
