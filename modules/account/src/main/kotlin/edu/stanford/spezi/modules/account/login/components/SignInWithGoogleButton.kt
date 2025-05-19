package edu.stanford.spezi.modules.account.login.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import edu.stanford.spezi.modules.design.R
import edu.stanford.spezi.modules.design.component.AsyncButton
import edu.stanford.spezi.ui.theme.Sizes
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

@Composable
fun SignInWithGoogleButton(
    onButtonClick: () -> Unit,
    isLoading: Boolean = false,
) {
    AsyncButton(
        isLoading = isLoading,
        onClick = {
            onButtonClick()
        },
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = stringResource(edu.stanford.spezi.modules.account.R.string.account_google_icon),
            modifier = Modifier.size(Sizes.Icon.small)
        )
        Spacer(modifier = Modifier.width(Spacings.small))
        Text(stringResource(edu.stanford.spezi.modules.account.R.string.account_sign_in_with_google))
    }
}

@ThemePreviews
@Composable
fun SignInWithGoogleButtonPreview() {
    SpeziTheme {
        SignInWithGoogleButton(onButtonClick = {})
    }
}
