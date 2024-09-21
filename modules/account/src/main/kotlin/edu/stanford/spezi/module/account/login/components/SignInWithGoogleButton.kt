package edu.stanford.spezi.module.account.login.components

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
import edu.stanford.spezi.core.design.R
import edu.stanford.spezi.core.design.component.AsyncButton
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews

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
            contentDescription = stringResource(edu.stanford.spezi.module.account.R.string.google_icon),
            modifier = Modifier.size(Sizes.Icon.small)
        )
        Spacer(modifier = Modifier.width(Spacings.small))
        Text(stringResource(edu.stanford.spezi.module.account.R.string.sign_in_with_google))
    }
}

@ThemePreviews
@Composable
fun SignInWithGoogleButtonPreview() {
    SpeziTheme(isPreview = true) {
        SignInWithGoogleButton(onButtonClick = {})
    }
}
