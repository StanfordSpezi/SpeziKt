package edu.stanford.spezikt.spezi_module.contact

import edu.stanford.spezikt.spezi_module.contact.model.ContactOption

sealed interface OnAction {
    data class CardClicked(val option: ContactOption) : OnAction
    data class NavigateTo(val address: String) : OnAction
}