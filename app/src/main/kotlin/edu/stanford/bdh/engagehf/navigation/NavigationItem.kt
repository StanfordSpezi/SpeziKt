package edu.stanford.bdh.engagehf.navigation

import androidx.annotation.DrawableRes
import edu.stanford.bdh.engagehf.NavigationItemEnum

data class NavigationItem(
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
    val label: String,
    var selected: Boolean = false,
    val onClick: () -> Unit = {},
    val navigationItem: NavigationItemEnum,
)
