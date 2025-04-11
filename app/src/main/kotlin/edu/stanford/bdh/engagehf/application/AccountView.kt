package edu.stanford.bdh.engagehf.application
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text

import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import edu.stanford.bdh.engagehf.application.modules.Account
import edu.stanford.bdh.engagehf.new_onboarding.OnboardingView

class AccountView: OnboardingView() {

    val account by Dependency<Account>()

    @Composable
    fun Content() {
        val activeSeconds by account.activeSeconds.collectAsState()


        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hello ${account.username}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp)) // <-- space between texts

            Text(
                text = if (account.onboarded) "Onboarded" else "Not onboarded",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp)) // <-- space between texts
            Text(
                text = "Active seconds: $activeSeconds",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

    }
}

