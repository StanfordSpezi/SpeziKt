package edu.stanford.bdh.heartbeat.app.onboarding

import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun OnboardingPage() {
    val viewModel = hiltViewModel<OnboardingViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    OnboardingPage(uiState, viewModel::onAction)
}

@Composable
private fun OnboardingPage(
    uiState: OnboardingUiState,
    onAction: (OnboardingAction) -> Unit,
) {
    LaunchedEffect(Unit) {
        onAction(OnboardingAction.Reload)
    }

    val styledText = """
        <p class="title">STANFORD UNIVERSITY CONSENT<br>TO PARTICIPATE IN THE STANFORD HEARTBEAT STUDY</p>
        <table>
            <tr><td class="head-column">TITLE:</td><td class="text-column">STANFORD HEARTBEAT STUDY</td></tr>
            <tr><td class="head-column">PROTOCOL NO.:</td><td class="text-column">75132</td></tr>
            <tr><td class="head-column">SPONSOR:</td><td class="text-column">Janssen Research and Development LLC</td></tr>
            <tr><td class="head-column">PROTOCOL DIRECTOR:</td><td class="text-column">
                Marco Perez, MD<br>300 Pasteur Drive, Stanford, CA 94305, USA
            </td></tr>
            <tr><td class="head-column">CONTACT:</td><td class="text-column">(650)-307-7878</td></tr>
        </table>
        <p class="header">DESCRIPTION:</p>
        <p class="text">
            You are invited to participate in the Stanford Heartbeat Study. This registry connects volunteers for 
            Atrial Fibrillation research. If you wish to revoke your authorization, contact:
        </p>
        <b>Marco Perez, MD<br>300 Pasteur Drive, Stanford, CA 94305, USA</b>
        <p class="text"><b>Personal Information Used:</b> Contact info, demographics, medical history, and optional 
            smartphone data (GPS, PPG, ECG).</p>
        <p class="text"><b>Data Access:</b> Stanford University, study staff, regulatory bodies, and the study sponsor.</p>
        <p class="text"><b>Authorization Expiry:</b> December 31, 2075, or study completion.</p>
        <p class="text bold">Do you provide your authorization to use your health information for research?</p>
    """

    Column {
        RenderHtmlContent(styledText)
    }
}


@ThemePreviews
@Composable
private fun OnboardingLoadingFailed() {
    SpeziTheme(isPreview = true) {
        Text("State")
    }
}

@Composable
fun RenderHtmlContent(htmlContent: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                // Enable JavaScript if needed
                settings.javaScriptEnabled = true
                loadDataWithBaseURL(
                    null,
                    htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        }
    )
}