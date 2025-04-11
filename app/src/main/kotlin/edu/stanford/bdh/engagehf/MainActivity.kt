package edu.stanford.bdh.engagehf

import adamma.c4dhi.claid_android.CLAIDServices.ServiceAnnotation
import adamma.c4dhi.claid_android.Configuration.CLAIDPersistanceConfig
import adamma.c4dhi.claid_android.Configuration.CLAIDSpecialPermissionsConfig
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import edu.stanford.spezi.core.coroutines.Dispatching
import edu.stanford.spezi.ui.Sizes
import edu.stanford.speziclaid.CLAIDRuntime
import edu.stanford.speziclaid.cough.CoughRecognizerPipeline
import edu.stanford.speziclaid.helper.structOf
import edu.stanford.speziclaid.module.DataRecorder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import edu.stanford.bdh.engagehf.application.AccountView
import edu.stanford.bdh.engagehf.new_onboarding.OnboardingFlow
import edu.stanford.speziclaid.datastore.DataRetriever
import edu.stanford.speziclaid.datastore.retrieve.getCoughSamples

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private val viewModel by viewModels<MainActivityViewModel>()

    @Inject
    @Dispatching.Main
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    lateinit var claidRuntime: CLAIDRuntime

    @Inject
    lateinit var dataRetriever: DataRetriever

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        viewModel.onAction(action = MainActivityAction.NewIntent(intent))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Show the user data inside a Text view
            AppContent()
        }
    }

    @Composable
    private fun AppContent() {

        val view = remember { OnboardingFlow(
            listOf(
                AccountView()
            )
        ) }
        view.Content()
    }

}
