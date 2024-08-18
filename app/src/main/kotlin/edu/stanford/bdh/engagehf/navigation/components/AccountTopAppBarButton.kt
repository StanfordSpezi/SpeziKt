package edu.stanford.bdh.engagehf.navigation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.bdh.engagehf.navigation.screens.AccountUiState
import edu.stanford.bdh.engagehf.navigation.screens.Action
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.Sizes

@Composable
fun AccountTopAppBarButton(accountUiState: AccountUiState, onAction: (Action) -> Unit) {
    IconButton(onClick = {
        onAction(Action.ShowAccountDialog(true))
    }) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Account",
            tint = Colors.onPrimary,
            modifier = Modifier.size(Sizes.Icon.medium)
        )
    }
    AnimatedVisibility(
        visible = accountUiState.showDialog,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        AccountDialog(accountUiState = accountUiState, onAction = onAction)
    }
}
