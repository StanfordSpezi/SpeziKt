package edu.stanford.spezi.modules.account.login.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezi.modules.account.R
import edu.stanford.spezi.ui.Spacings

@Composable
fun TextDivider(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(end = Spacings.small)
        )
        Text(text = text, color = DividerDefaults.color)
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacings.small)
        )
    }
}

@Preview
@Composable
fun TextDividerPreview() {
    TextDivider(stringResource(R.string.or))
}
