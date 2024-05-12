package edu.stanford.spezikt.core.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezikt.core.design.theme.SpeziKtTheme

/**
 * A button of the SpeziKt design system with customizable content.
 *
 * @sample edu.stanford.spezikt.core.design.component.SpeziButtonDarkPreview
 *
 * @param onClick The callback to be invoked when the button is clicked.
 * @param modifier The modifier to be applied to the button.
 * @param enabled Whether the button is enabled.
 * @param contentPadding The padding to be applied to the button content.
 * @param content The content of the button.
 */
@Composable
fun SpeziButton(
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
            containerColor = MaterialTheme.colorScheme.onBackground,
        ),
        contentPadding = contentPadding,
        content = content,
    )
}

@Preview
@Composable
fun SpeziButtonDarkPreview() {
    SpeziKtTheme {
        Column {
            SpeziButton(
                onClick = { },
                content = {
                    Text(text = "Text", style = MaterialTheme.typography.bodyLarge)
                }
            )
        }
    }
}

