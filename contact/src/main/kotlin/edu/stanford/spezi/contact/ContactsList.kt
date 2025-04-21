package edu.stanford.spezi.contact

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.ui.ComposableContent
import edu.stanford.spezi.ui.testIdentifier

data class ContactsList(
    val contacts: List<Contact>,
) : ComposableContent {
    @Composable
    override fun Content(modifier: Modifier) {
        LazyColumn(modifier = modifier) {
            items(contacts) {
                it.Content(
                    modifier = Modifier.testIdentifier(
                        ContactsListTestIdentifier.CONTACT,
                        suffix = it.id.toString()
                    )
                )
            }
        }
    }
}

enum class ContactsListTestIdentifier {
    CONTACT,
}
