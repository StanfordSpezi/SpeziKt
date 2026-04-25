package edu.stanford.spezi.sample.app.health

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.sample.app.R
import edu.stanford.spezi.ui.SpeziScaffold
import edu.stanford.spezi.ui.rememberMutableSpeziScaffoldState
import edu.stanford.spezi.ui.rememberSpeziAppBar
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.TextStyles

@Composable
fun HealthPrivacyScreen() {
    val activity = LocalActivity.current
    val appBar = rememberSpeziAppBar(activity) {
        title(R.string.app_name)
        back { activity?.finish() }
    }
    SpeziScaffold(state = rememberMutableSpeziScaffoldState(appBar = appBar)) {
        Text(
            modifier = Modifier.padding(Spacings.medium),
            text = "This app uses Health Connect to read and write health data for demo purposes.",
            style = TextStyles.bodyMedium
        )
    }
}
