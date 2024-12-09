package edu.stanford.spezi.module.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.core.design.component.ComposeContentActivity
import edu.stanford.spezi.module.account.composables.provider.AccountServiceType
import edu.stanford.spezi.module.account.composables.provider.AccountValueConfigurationType
import edu.stanford.spezi.module.account.composables.provider.DefaultCredentials
import edu.stanford.spezi.module.account.composables.provider.TestConfiguration
import edu.stanford.spezi.module.account.composables.provider.TestConfigurationComposable
import edu.stanford.spezi.module.account.simulators.AccountSimulator
import org.junit.Rule

@HiltAndroidTest
abstract class AccountTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComposeContentActivity>()

    @Composable
    abstract fun Content()

    fun configuredAccount(
        serviceType: AccountServiceType = AccountServiceType.MAIL,
        valueConfiguration: AccountValueConfigurationType = AccountValueConfigurationType.DEFAULT,
        credentials: DefaultCredentials = DefaultCredentials.DISABLED,
        accountRequired: Boolean = false,
        noName: Boolean = false,
        block: AccountSimulator.() -> Unit,
    ) {
        val configuration = TestConfiguration(
            serviceType = serviceType,
            valueConfiguration = valueConfiguration,
            credentials = credentials,
            accountRequired = accountRequired,
            noName = noName,
        )
        composeTestRule.activity.setScreen {
            TestConfigurationComposable(configuration) {
                Content()
            }
        }
        AccountSimulator(composeTestRule).apply {
            waitUntilConfigurationCompleted()
            block()
        }
    }
}
