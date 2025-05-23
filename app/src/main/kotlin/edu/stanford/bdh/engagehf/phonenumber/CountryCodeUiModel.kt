package edu.stanford.bdh.engagehf.phonenumber

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.noRippleClickable
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class CountryCodeUiModel(
    val emojiFlag: String,
    val countryCode: String,
    val countryName: String,
    val number: String,
    val onClick: () -> Unit,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        Row(
            modifier = Modifier
                .noRippleClickable(onClick = onClick)
                .padding(vertical = Spacings.small)
        ) {
            Text(text = emojiFlag)
            Spacer(modifier = Modifier.width(Spacings.small))
            Text(
                text = countryName,
                style = TextStyles.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = number, color = Colors.secondary)
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val item = CountryCodeUiModel(
        emojiFlag = "🇺🇸",
        countryCode = "US",
        countryName = "United States",
        number = "+1",
        onClick = {},
    )
    SpeziTheme {
        item.Content(modifier = Modifier.fillMaxWidth())
    }
}
