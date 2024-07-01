package edu.stanford.spezi.core.design.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.theme.Colors.onPrimary
import edu.stanford.spezi.core.design.theme.Colors.primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(title: String) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = primary,
            scrolledContainerColor = primary,
            navigationIconContentColor = onPrimary,
            titleContentColor = onPrimary,
            actionIconContentColor = onPrimary
        ),
        title = { Text(text = title) }
    )
}