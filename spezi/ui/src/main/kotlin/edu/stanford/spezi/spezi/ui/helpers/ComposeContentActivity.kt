package edu.stanford.spezi.spezi.ui.helpers

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.CallSuper
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import edu.stanford.spezi.spezi.ui.helpers.theme.SpeziTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class ComposeContentActivity : FragmentActivity() {

    private val content = MutableStateFlow<ComposableBlock?>(null)

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpeziTheme {
                val content by content.collectAsState()
                content?.invoke()
            }
        }
    }

    fun setScreen(content: ComposableBlock) {
        this.content.update { content }
    }
}
