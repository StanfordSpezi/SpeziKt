package edu.stanford.spezi.modules.contact

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import edu.stanford.spezi.modules.contact.model.Contact
import edu.stanford.spezi.modules.contact.model.ContactOptionType
import edu.stanford.spezi.modules.contact.repository.ContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {
    private val _contact = MutableStateFlow<Contact?>(null)
    val contact: StateFlow<Contact?> = _contact

    init {
        fetchContact()
    }

    private fun fetchContact() {
        _contact.value = repository.getContact()
    }

    fun handleAction(action: OnAction, context: Context) {
        when (action) {
            is OnAction.CardClicked -> {
                when (action.option.optionType) {
                    ContactOptionType.EMAIL -> {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:${action.option.value}")
                        }
                        context.startActivity(emailIntent)
                    }

                    ContactOptionType.CALL -> {
                        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${action.option.value}")
                        }
                        context.startActivity(dialIntent)
                    }

                    ContactOptionType.WEBSITE -> {
                        val browserIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(action.option.value))
                        context.startActivity(browserIntent)
                    }
                }
            }

            is OnAction.NavigateTo -> {
                val gmmIntentUri = Uri.parse("geo:0,0?q=${action.address}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
            }
        }
    }
}