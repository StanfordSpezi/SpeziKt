package edu.stanford.spezi.modules.contact.model

import android.content.Context

interface ContactOptionAction {
    fun handle(context: Context)
}