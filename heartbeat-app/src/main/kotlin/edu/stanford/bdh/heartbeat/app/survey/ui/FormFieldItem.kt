package edu.stanford.bdh.heartbeat.app.survey.ui

interface FormFieldItem : SurveyItem {
    val id: String
    val required: Boolean
}
