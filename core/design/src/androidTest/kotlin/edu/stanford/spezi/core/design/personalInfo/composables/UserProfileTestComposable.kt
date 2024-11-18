package edu.stanford.spezi.core.design.personalInfo.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.core.design.views.personalinfo.UserProfileComposable
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
            ImageResource.Vector(Icons.Default.Person)
        }
    }
}
