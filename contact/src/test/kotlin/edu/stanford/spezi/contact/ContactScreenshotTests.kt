package edu.stanford.spezi.contact

import android.location.Address
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.ui.Modifier
import edu.stanford.spezi.resources.Strings
import edu.stanford.spezi.testing.screenshot.ScreenshotTest
import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource
import org.junit.Test
import java.util.Locale

@Suppress("MaxLineLength")
class ContactScreenshotTests : ScreenshotTest() {

    @Test
    fun `Contact screenshot test`() {
        val contact = Contact(
            name = "Leland Stanford",
            image = ImageResource(Icons.Default.AccountBox, StringResource(Strings.contact_profile_picture)),
            title = StringResource("University Founder"),
            description = StringResource(
                "Amasa Leland Stanford (March 9, 1824 – June 21, 1893) was an American industrialist and politician. He and his wife Jane were also the founders of Stanford University, which they named after their late son."
            ),
            organization = StringResource("Stanford University"),
            address = Address(Locale.US).apply {
                setAddressLine(0, "450 Jane Stanford Way")
                locality = "Stanford"
                adminArea = "CA"
            },
            options = listOf(
                ContactOption.call("+49 123 456 789"),
                ContactOption.email(listOf("test@gmail.com")),
                ContactOption.website("https://www.test.test")
            )
        )
        screenshot { contact.Content(modifier = Modifier.fillMaxSize()) }
    }
}
