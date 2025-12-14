package edu.stanford.spezi.module.account.account.views.setup

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.Spacings

@Composable
internal fun ServicesDivider() {
    Row(Modifier.padding(horizontal = 36.dp, vertical = 16.dp)) {
        HorizontalDivider()
        Text(
            StringResource("OR").text(),
            Modifier.padding(horizontal = Spacings.small),
        )
        HorizontalDivider()
    }
}
