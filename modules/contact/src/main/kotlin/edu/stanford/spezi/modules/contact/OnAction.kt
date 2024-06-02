package edu.stanford.spezi.modules.contact

import edu.stanford.spezi.modules.contact.model.ContactOption

sealed interface OnAction {
    data class CardClicked(val option: ContactOption) : OnAction
    data class NavigateTo(val address: String) : OnAction
}
