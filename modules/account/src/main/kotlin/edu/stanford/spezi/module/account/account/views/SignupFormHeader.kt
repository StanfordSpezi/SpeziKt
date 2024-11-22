package edu.stanford.spezi.module.account.account.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.stanford.spezi.core.design.component.ImageResource
import edu.stanford.spezi.core.design.component.ImageResourceComposable
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.theme.Colors
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
internal fun FormHeader(
    image: ImageResource,
    title: StringResource,
    instructions: StringResource,
) {
    Column(Modifier.fillMaxWidth()) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageResourceComposable(
                image,
                modifier = Modifier.size(50.dp, 50.dp),
                tint = Colors.primary,
            )

            Text(
                title.text(),
                Modifier.padding(bottom = 4.dp),
                textAlign = TextAlign.Center,
                style = TextStyles.headlineLarge.copy(fontWeight = FontWeight.Bold),
            )
        }

        Text(
            instructions.text(),
            Modifier.padding(horizontal = 25.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun SignupFormHeader() {
    FormHeader(
        // TODO: We should probably use person_add instead: https://fonts.google.com/icons?selected=Material+Icons:person_add:
        ImageResource.Vector(Icons.Default.Person, StringResource("User Profile")),
        StringResource("Create a new Account"),
        StringResource("Please fill out the details below to create your new account."),
    )
}

@ThemePreviews
@Composable
private fun SignupHeaderFormPreview() {
    SpeziTheme(isPreview = true) {
        SignupFormHeader()
    }
}
