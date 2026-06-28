# Module contact

## Overview

The `contact` module provides Compose UI for displaying contact information such as
support staff, study coordinators, or care providers. A `Contact` renders the person's
name, title, organization, description, an optional address card with map navigation,
and a set of interactive contact options for calling, texting, emailing, or opening a
website.

## Components

- **`Contact`**: A `ComposableContent` data class describing a contact (`name`, `image`,
  `title`, `description`, `organization`, `address`, and a list of `options`). Its
  `Content(modifier)` composable renders the full contact card.
- **`ContactOption`**: A `ComposableContent` data class representing a tappable action
  (icon, title, and an `action: (Context) -> Unit`) shown as an elevated card. Provided
  factory extensions on the companion object:
  - `ContactOption.call(number)` – opens the dialer (`Icons.Default.Call`).
  - `ContactOption.text(number)` – opens an SMS intent (`Icons.AutoMirrored.Default.Send`).
  - `ContactOption.email(addresses, subject = null)` – opens a mail composer (`Icons.Default.Email`).
  - `ContactOption.website(uriString)` – opens the URL in a browser (`Icons.Default.Info`).
- **`AddressCard`**: An internal composable that renders an Android `Address` and opens it
  in Google Maps via a `geo:` intent.
- **`Address.formatted()`**: Extension that converts an Android `Address` into a
  newline-separated, human-readable string (address lines, locality/admin area/postal
  code, and country).

## Usage

Build a `Contact` and render it inside a `SpeziTheme`:

```kotlin
import android.location.Address
import androidx.compose.ui.Modifier
import edu.stanford.spezi.contact.Contact
import edu.stanford.spezi.contact.ContactOption
import edu.stanford.spezi.contact.call
import edu.stanford.spezi.contact.email
import edu.stanford.spezi.contact.website
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.personalinfo.PersonNameComponents
import edu.stanford.spezi.ui.theme.SpeziTheme
import java.util.Locale

val contact = Contact(
    name = PersonNameComponents(givenName = "Leland", familyName = "Stanford"),
    title = StringResource("University Founder"),
    description = StringResource("Founder of Stanford University."),
    organization = StringResource("Stanford University"),
    address = Address(Locale.US).apply {
        setAddressLine(0, "450 Jane Stanford Way")
        locality = "Stanford"
        adminArea = "CA"
    },
    options = listOf(
        ContactOption.call("+1 (650) 723-2300"),
        ContactOption.email(listOf("contact@stanford.edu")),
        ContactOption.website("https://www.stanford.edu"),
    ),
)

@Composable
fun ContactScreen() {
    SpeziTheme {
        contact.Content(modifier = Modifier)
    }
}
```
