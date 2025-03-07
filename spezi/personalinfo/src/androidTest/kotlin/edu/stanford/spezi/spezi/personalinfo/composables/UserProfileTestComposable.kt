package edu.stanford.spezi.spezi.personalinfo.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.spezi.personalinfo.PersonNameComponents
import edu.stanford.spezi.spezi.personalinfo.UserProfileComposable
import edu.stanford.spezi.spezi.ui.resources.ImageResource
import edu.stanford.spezi.spezi.ui.resources.StringResource
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun UserProfileTestComposable() {
    Column {
        UserProfileComposable(
            PersonNameComponents(
                givenName = "Paul",
                familyName = "Schmiedmayer"
            ),
            Modifier.height(100.dp),
        )
        UserProfileComposable(
            PersonNameComponents(
                givenName = "Leland",
                familyName = "Stanford"
            ),
            Modifier.height(200.dp),
        ) {
            delay(0.5.seconds)
            ImageResource.Vector(Icons.Default.Person, StringResource("Person"))
        }
    }
}
