package edu.stanford.spezi.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Sizes
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

data class ItemDisplayRow(
    val leadingImage: ImageResource?,
    val label: StringResource,
    val value: StringResource?,
    val showArrow: Boolean = false,
    val onClick: (() -> Unit)? = null,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        ItemDisplayRowComposable(
            modifier = modifier,
            leadingImage = leadingImage,
            label = label.text(),
            valueContent = {
                value?.let {
                    Text(
                        text = it.text(),
                        color = Colors.secondary,
                    )
                }
            },
            showArrow = showArrow,
            onClick = onClick,
        )
    }
}

@Composable
fun ItemDisplayRowComposable(
    modifier: Modifier = Modifier,
    leadingImage: ImageResource?,
    label: String,
    valueContent: @Composable (RowScope.() -> Unit)?,
    showArrow: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val rowModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }
    Row(
        modifier = rowModifier
            .padding(Spacings.small),
        horizontalArrangement = Arrangement.spacedBy(Spacings.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingImage?.Content()
        Text(text = label)
        Spacer(modifier = Modifier.weight(1f))
        valueContent?.invoke(this)
        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                modifier = Modifier.size(Sizes.Icon.extraSmall),
                tint = Colors.secondary,
                contentDescription = null,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    val row = ItemDisplayRow(
        leadingImage = ImageResource(Icons.Default.AccountCircle),
        label = StringResource("Your account"),
        value = StringResource("john.smith2"),
        showArrow = true,
        onClick = {},
    )

    SpeziTheme {
        row.Content(modifier = Modifier.fillMaxWidth())
    }
}
