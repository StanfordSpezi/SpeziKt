package edu.stanford.spezi.module.account

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.module.account.account.AccountOverview
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccount
import edu.stanford.spezi.module.account.composables.provider.DefaultCredentials
import edu.stanford.spezi.module.account.utils.invitationCode
import org.junit.Test

@HiltAndroidTest
class AccountOverviewTest : AccountTest() {

    @Composable
    override fun Content() {
        AccountOverview {
            val account = LocalAccount.current
            Text("Spezi Account")

            account?.details?.invitationCode?.let {
                Text("Invitation Code: $it")
            }
        }
    }

    @Test
    fun testRequirementLevelsOverview() {
        configuredAccount(
            credentials = DefaultCredentials.CREATE_AND_SIGN_IN,
        ) {
            assertTextExists("Spezi Account")

            assertTextExists("Leland Stanford")
            assertTextExists("lelandstanford@stanford.edu")

            assertTextExists("Name, E-Mail Address")
            assertTextExists("Sign-In & Security")

            assertTextExists("Gender Identity, Male")
            assertTextExists("Date of Birth, Mar 9, 1824")

            assertTextExists("License Information")
        }
    }
}
