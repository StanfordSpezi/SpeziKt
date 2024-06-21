package edu.stanford.spezi.module.account.register

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edu.stanford.spezi.core.design.component.validated.outlinedtextfield.ValidatedOutlinedTextField
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings

@Composable
internal fun IconLeadingContent(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    content: @Composable () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier.fillMaxWidth()
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(Sizes.Icon.small)
                    .offset(y = Spacings.small)
            )
            Spacer(modifier = Modifier.width(Spacings.medium))
        } else {
            // Reserve the space for the icon
            Spacer(modifier = Modifier.width(Sizes.Icon.small + Spacings.small))
        }
        content()
    }
}

@Preview
@Composable
private fun IconLeadingContentPreview(
    @PreviewParameter(IconLeadingContentPreviewProvider::class) params: Pair<ImageVector?, String>,
) {
    IconLeadingContent(
        icon = params.first,
        content = {
            ValidatedOutlinedTextField(
                labelText = params.second,
                value = "",
                onValueChange = {},
                errorText = null
            )
        }
    )
}

private class IconLeadingContentPreviewProvider :
    PreviewParameterProvider<Pair<ImageVector?, String>> {
    override val values: Sequence<Pair<ImageVector?, String>> = sequenceOf(
        Pair(Icons.Default.AccountBox, "Account Information"),
        Pair(Icons.Default.Person, "Personal Information"),
        Pair(null, "No Icon")
    )
}
