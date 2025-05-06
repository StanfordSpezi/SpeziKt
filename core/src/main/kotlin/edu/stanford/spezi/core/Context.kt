package edu.stanford.spezi.core

import android.app.Application
import android.content.Context

/**
 * Returns the application [Context] of the [SpeziApplication] if [Application] conforms to [SpeziApplication] or null otherwise.
 */
val SpeziApplication.applicationContext: Context?
    get() = this as? Application

/**
 * Returns the application [Context] of the [SpeziApplication].
 *
 * Note that this method will throw in case the [SpeziApplication] is not an instance of [Application].
 */
fun SpeziApplication.requireApplicationContext(): Context =
    applicationContext ?: speziError("Only android.app.Application is supported as a SpeziApplication")
