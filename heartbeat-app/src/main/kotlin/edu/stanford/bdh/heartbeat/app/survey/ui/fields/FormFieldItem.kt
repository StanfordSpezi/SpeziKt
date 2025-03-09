package edu.stanford.bdh.heartbeat.app.survey.ui.fields

import edu.stanford.bdh.heartbeat.app.survey.ui.SurveyItem

sealed interface FormFieldItem : SurveyItem {
    val fieldId: String
}

const val DISABLED_ALPHA = 0.5f
