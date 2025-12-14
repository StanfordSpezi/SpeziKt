package edu.stanford.spezi.module.account.account.views.overview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.component.ImageResourceComposable
import edu.stanford.spezi.core.design.component.StringResource

@Composable
fun DetailsSectionIcon(modifier: Modifier = Modifier) {
    ImageResourceComposable(
        ImageResource.Vector(
            Icons.Default.AccountBox,
            StringResource("Account Details")
        ),
        modifier,
    )
}

@Composable
fun SecuritySectionIcon(modifier: Modifier = Modifier) {
    ImageResourceComposable(
        ImageResource.Vector(
            Icons.Default.Build,
            StringResource("Security")
        ),
        modifier,
    )
}
