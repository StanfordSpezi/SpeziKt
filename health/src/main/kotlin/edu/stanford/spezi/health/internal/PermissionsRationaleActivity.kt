package edu.stanford.spezi.health.internal

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import edu.stanford.spezi.core.dependency
import edu.stanford.spezi.health.Health
import edu.stanford.spezi.health.R
import edu.stanford.spezi.ui.CommonScaffold
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.TextStyles

internal class PermissionsRationaleActivity : AppCompatActivity() {
    private val health by dependency<Health>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeziTheme {
                Screen(config = health.privacyConfig)
            }
        }
    }

    @Composable
    private fun Screen(config: PrivacyConfig) {
        when (config) {
            is PrivacyConfig.Text -> RationaleScreen(
                title = config.title.text(),
                description = config.description.text()
            )
            is PrivacyConfig.Composable -> config.composable.invoke()
            is PrivacyConfig.Content -> config.content.Content()
            is PrivacyConfig.Default -> RationaleScreen(
                title = stringResource(R.string.default_privacy_policy_title),
                description = stringResource(R.string.default_privacy_policy_description),
            )
        }
    }

    @Composable
    private fun RationaleScreen(
        title: String,
        description: String,
    ) {
        CommonScaffold(
            title = title,
            navigationIcon = {
                IconButton(onClick = { finish() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "",
                    )
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .padding(Spacings.medium)
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = description,
                        style = TextStyles.bodyMedium
                    )
                }
            }
        )
    }
}
