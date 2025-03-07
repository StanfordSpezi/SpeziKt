package edu.stanford.spezi.spezi.contact

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.spezi.contact.model.Contact
import edu.stanford.spezi.spezi.ui.helpers.testIdentifier

@Composable
fun ContactsList(contacts: List<Contact>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(contacts) {
            ContactComposable(
                it,
                modifier = Modifier.testIdentifier(
                    ContactsListTestIdentifier.CONTACT,
                    suffix = it.id.toString()
                )
            )
        }
    }
}

enum class ContactsListTestIdentifier {
    CONTACT,
}
