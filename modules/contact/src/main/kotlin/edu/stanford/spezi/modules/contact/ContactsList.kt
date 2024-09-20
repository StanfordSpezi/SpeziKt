package edu.stanford.spezi.modules.contact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.stanford.spezi.modules.contact.model.Contact

@Composable
fun ContactsList(contacts: List<Contact>) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        contacts.forEach { ContactView(contact = it) }
    }
}
