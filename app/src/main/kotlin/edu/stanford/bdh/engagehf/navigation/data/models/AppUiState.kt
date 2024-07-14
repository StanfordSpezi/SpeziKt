package edu.stanford.bdh.engagehf.navigation.data.models

import edu.stanford.bdh.engagehf.BottomBarItem
import edu.stanford.bdh.engagehf.BottomSheetContent

data class AppUiState(
    val items: List<BottomBarItem>,
    val selectedItem: BottomBarItem,
    val isBottomSheetExpanded: Boolean = false,
    val bottomSheetContent: BottomSheetContent? = null,
)
