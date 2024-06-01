package edu.stanford.spezi.module.account.login.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import edu.stanford.spezi.core.design.R
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun SignInWithGoogleButton(
    onButtonClick: () -> Unit,
) {
    Button(
        onClick = {
            onButtonClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacings.large)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google Icon",
            modifier = Modifier.size(Sizes.iconSmall)
        )
        Spacer(modifier = Modifier.width(Spacings.small))
        Text("Sign in with Google")
    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Preview
@Composable
fun SignInWithGoogleButtonPreview() {
    SignInWithGoogleButton(onButtonClick = {})
}