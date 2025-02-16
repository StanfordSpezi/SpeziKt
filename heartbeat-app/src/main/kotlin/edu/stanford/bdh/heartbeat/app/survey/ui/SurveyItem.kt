package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface SurveyItem {

    @Composable
    fun Content(modifier: Modifier)
}