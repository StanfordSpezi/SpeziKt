package edu.stanford.spezi.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

// TODO: This is incredibly confusing, since one doesn't easily see which button is in use.

/**
 * A button of the Spezi design system with customizable content.
 *
 * @sample SpeziButtonDarkPreview
 *
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param enabled Whether the button is enabled.
 * @param contentPadding The padding to be applied to the button content.
 * @param content The content of the button.
 */
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Colors.onBackground,
        ),
        contentPadding = contentPadding,
        content = content,
    )
}

@Preview
@Composable
fun SpeziButtonDarkPreview() {
    SpeziTheme {
        Column {
            Button(
                onClick = { },
                content = {
                    Text(text = "Text", style = TextStyles.bodyLarge)
                }
            )
        }
    }
}
