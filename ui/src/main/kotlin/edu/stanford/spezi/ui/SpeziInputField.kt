package edu.stanford.spezi.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Sizes
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziShapes
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles
import edu.stanford.spezi.ui.theme.ThemePreviews

data class SpeziInputField(
    val value: String,
    val placeholder: StringResource? = null,
    val hideContent: Boolean = false,
    val showBorder: Boolean = true,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val imeAction: ImeAction = ImeAction.Default,
    val onValueChanged: (String) -> Unit,
) : ComposableContent {

    @Composable
    override fun Content(modifier: Modifier) {
        SpeziInputFieldComposable(
            modifier = modifier,
            value = value,
            placeholder = placeholder,
            keyboardType = keyboardType,
            imeAction = imeAction,
            hideContent = hideContent,
            showBorder = showBorder,
            onValueChanged = onValueChanged,
        )
    }
}

@Composable
fun SpeziInputFieldComposable(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: StringResource? = null,
    contentPaddingValues: PaddingValues = PaddingValues(Spacings.small),
    hideContent: Boolean = false,
    showBorder: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    onValueChanged: (String) -> Unit,
) {
    var isContentVisible by remember { mutableStateOf(false) }

    val visualTransformation = remember(hideContent, isContentVisible) {
        if (hideContent && !isContentVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        }
    }

    val resolvedKeyboardType = remember(hideContent, keyboardType) {
        if (hideContent && keyboardType == KeyboardType.Text) {
            KeyboardType.Password
        } else {
            keyboardType
        }
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChanged,
        singleLine = true,
        textStyle = TextStyles.bodyLarge.copy(color = LocalContentColor.current),
        keyboardOptions = KeyboardOptions(
            keyboardType = resolvedKeyboardType,
            imeAction = imeAction,
        ),
        visualTransformation = visualTransformation,
        modifier = modifier
            .bringIntoViewOnFocusedEvent()
            .heightIn(min = 52.dp)
            .border(
                width = Sizes.Border.small,
                shape = SpeziShapes.medium,
                color = if (showBorder) {
                    Colors.outlineVariant
                } else {
                    Colors.transparent
                }
            ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(contentPaddingValues),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacings.small),
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder.text(),
                            color = Colors.secondary,
                            style = TextStyles.bodyLarge,
                        )
                    }
                    innerTextField()
                }

                if (hideContent) {
                    val icon = if (isContentVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Colors.secondary,
                        modifier = Modifier
                            .size(Sizes.Icon.small)
                            .clickable { isContentVisible = !isContentVisible }
                    )
                }
            }
        }
    )
}

private class PreviewParams : PreviewParameterProvider<SpeziInputField> {
    private val base = SpeziInputField(
        value = "your.email@stanford.edu",
        placeholder = StringResource("Enter value"),
        onValueChanged = {},
    )

    override val values: Sequence<SpeziInputField> = sequenceOf(
        base,
        base.copy(
            keyboardType = KeyboardType.Email,
        ),
        base.copy(
            value = "super-secret-password",
            hideContent = true,
        ),
        base.copy(
            value = "",
        ),
    )
}

@ThemePreviews
@Composable
private fun Preview(@PreviewParameter(PreviewParams::class) input: SpeziInputField) {
    SpeziTheme {
        input.Content(modifier = Modifier.width(200.dp))
    }
}
