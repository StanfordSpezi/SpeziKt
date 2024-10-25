package edu.stanford.bdh.engagehf.health.symptoms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews

@Composable
fun SymptomsDescriptionBottomSheet() {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.symptoms_description_title),
                style = TextStyles.titleLarge
            )
            VerticalSpacer()
            TitleDescriptionItem(
                title = stringResource(R.string.overall_score_title),
                description = stringResource(R.string.overall_score_description)
            )
            TitleDescriptionItem(
                title = stringResource(R.string.physical_limits_score_title),
                description = stringResource(R.string.physical_limits_score_description)
            )
            TitleDescriptionItem(
                title = stringResource(R.string.social_limits_score_title),
                description = stringResource(R.string.social_limits_score_description)
            )
            TitleDescriptionItem(
                title = stringResource(R.string.quality_of_life_score_title),
                description = stringResource(R.string.quality_of_life_score_description)
            )
            TitleDescriptionItem(
                title = stringResource(R.string.symptoms_frequency_score_title),
                description = stringResource(R.string.specific_symptoms_score_description)
            )
            TitleDescriptionItem(
                title = stringResource(R.string.dizziness_score_title),
                description = stringResource(R.string.dizziness_score_description)
            )
            VerticalSpacer()
        }
    }
}

@Composable
fun TitleDescriptionItem(title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacings.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = TextStyles.titleMedium
        )
        Text(
            text = description,
            style = TextStyles.bodyMedium.copy(textAlign = TextAlign.Center)
        )
    }
}

@ThemePreviews
@Composable
fun SymptomsDescriptionBottomSheetPreview() {
    SpeziTheme(isPreview = true) {
        SymptomsDescriptionBottomSheet()
    }
}
