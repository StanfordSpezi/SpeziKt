package edu.stanford.bdh.engagehf.navigation.data.models

import edu.stanford.bdh.engagehf.BottomBarItem

data class AppUiState(
    val items: List<BottomBarItem>,
    val selectedItem: BottomBarItem,
)
