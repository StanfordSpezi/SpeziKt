package edu.stanford.spezi.module.account.account.views.setup.provider

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.component.ImageResourceComposable
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.ThemePreviews
import edu.stanford.spezi.core.design.views.views.model.ViewState
import edu.stanford.spezi.core.design.views.views.views.button.SuspendButton

@Composable
fun AccountServiceButton(
    title: StringResource,
    modifier: Modifier = Modifier,
    image: ImageResource = remember { ImageResource.Vector(Icons.Default.Person, StringResource("User Profile")) },
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    onClick: suspend () -> Unit,
) {
    AccountServiceButton(
        modifier = modifier,
        state = state,
        colors = colors,
        onClick = onClick,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
        ) {
            ImageResourceComposable(image)
            Spacer(Modifier.width(8.dp))
            Text(title.text())
        }
    }
}

@Composable
fun AccountServiceButton(
    modifier: Modifier = Modifier,
    state: MutableState<ViewState> = remember { mutableStateOf(ViewState.Idle) },
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    onClick: suspend () -> Unit,
    label: @Composable RowScope.() -> Unit,
) {
    SuspendButton(
        modifier = modifier,
        state = state,
        colors = colors,
        onClick = onClick,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth()
        ) {
            label()
        }
    }
}

@ThemePreviews
@Composable
private fun AccountServiceButtonPreview() {
    SpeziTheme(isPreview = true) {
        AccountServiceButton(StringResource("E-Mail and Password")) {
            println("Pressed")
        }
    }
}
