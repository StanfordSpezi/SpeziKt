package edu.stanford.spezi.module.account

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.core.design.component.ComposeContentActivity
import edu.stanford.spezi.core.testing.onAllNodes
import edu.stanford.spezi.module.account.composables.AccountTestComposable
import edu.stanford.spezi.module.account.composables.provider.DefaultCredentials
import edu.stanford.spezi.module.account.composables.provider.TestConfiguration
import edu.stanford.spezi.module.account.composables.provider.TestConfigurationComposable
import edu.stanford.spezi.module.account.composables.provider.TestConfigurationIdentifier
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AccountTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    @Before
    fun init() {
        val configuration = TestConfiguration(
            credentials = DefaultCredentials.CREATE_AND_SIGN_IN
        )
        composeTestRule.activity.setScreen {
            TestConfigurationComposable(configuration) {
                AccountTestComposable()
            }
        }
    }

    @Test
    fun checkAccount() {
        composeTestRule.waitUntil(1_000) {
            composeTestRule
                .onAllNodes(TestConfigurationIdentifier.CONTENT)
                .fetchSemanticsNodes().size == 1
        }

        println("Done")
        TODO("Empty Test")
    }
}
