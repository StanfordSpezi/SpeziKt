package edu.stanford.spezi.sample.app.health

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import edu.stanford.spezi.sample.app.R
import edu.stanford.spezi.ui.CommonScaffold
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.TextStyles

@Composable
fun HealthPrivacyScreen() {
    val activity = LocalActivity.current
    CommonScaffold(
        title = stringResource(R.string.app_name),
        navigationIcon = {
            IconButton(onClick = { activity?.finish() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "",
                )
            }
        },
        content = {
            Text(
                modifier = Modifier.padding(Spacings.medium),
                text = "This app uses Health Connect to read and write health data for demo purposes.",
                style = TextStyles.bodyMedium
            )
        }
    )
}
