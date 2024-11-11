package edu.stanford.spezi.core.design.personalInfo.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents
import edu.stanford.spezi.core.design.validation.personalInfo.UserProfileComposable
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun UserProfileTestComposable() {
    Column {
        UserProfileComposable(
            Modifier.height(100.dp),
            edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents(
                givenName = "Paul",
                familyName = "Schmiedmayer"
            )
        )
        UserProfileComposable(
            Modifier.height(200.dp),
            edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents(
                givenName = "Leland",
                familyName = "Stanford"
            )
        ) {
            delay(0.5.seconds)
            return@UserProfileComposable ImageResource.Vector(Icons.Default.Person)
        }
    }
}
