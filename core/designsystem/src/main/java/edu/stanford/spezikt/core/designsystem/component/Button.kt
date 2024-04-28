package edu.stanford.spezikt.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezikt.core.designsystem.theme.SpeziKtTheme

/**
 * A button of the SpeziKt design system with customizable content.
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
        SpeziButton(
            onClick = { },
            content = {
                Text(text = "Text", style = MaterialTheme.typography.bodyLarge)
            }
        )
    }
}

