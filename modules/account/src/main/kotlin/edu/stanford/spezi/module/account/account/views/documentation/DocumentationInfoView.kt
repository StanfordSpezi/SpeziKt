package edu.stanford.spezi.module.account.account.views.documentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.spezi.core.design.component.Button
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.Spacings

@Composable
fun DocumentationInfoView(
    infoText: StringResource,
    uri: String
) {
    Column {
        Text(
            infoText.text(),
            textAlign = TextAlign.Center,
            color = Color.Gray // TODO: Using .secondary color on iOS, is there an equivalent on Android?
        )

        val uriHandler = LocalUriHandler.current
        Button(
            onClick = { uriHandler.openUri(uri) },
            modifier = Modifier.padding(Spacings.medium)
        ) {
            Text(StringResource("OPEN_DOCUMENTATION").text())
        }
    }
}
