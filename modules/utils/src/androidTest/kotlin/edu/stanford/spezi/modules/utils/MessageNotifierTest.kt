package edu.stanford.spezi.modules.utils

import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class MessageNotifierTest {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var messageNotifier: MessageNotifier

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `it should not fail when toasting in main thread`() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            messageNotifier.notify("Some message")
        }
    }

    @Test
    fun `it should not fail when notifying on a background thread`() = runTest(StandardTestDispatcher()) {
        messageNotifier.notify("Some message")
    }
}
