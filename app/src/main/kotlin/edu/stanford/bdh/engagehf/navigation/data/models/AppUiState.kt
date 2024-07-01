package edu.stanford.bdh.engagehf.navigation.data.models

import edu.stanford.bdh.engagehf.education
import edu.stanford.bdh.engagehf.heartHealth
import edu.stanford.bdh.engagehf.home
import edu.stanford.bdh.engagehf.medication
import edu.stanford.bdh.engagehf.navigation.NavigationItem

data class AppUiState(
    val selectedIndex: Int = 0,
    val navigationItems: List<NavigationItem> = listOf(
        home,
        heartHealth,
        medication,
        education,
    ),
)
