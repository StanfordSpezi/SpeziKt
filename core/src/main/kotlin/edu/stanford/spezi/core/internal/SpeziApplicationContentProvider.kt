package edu.stanford.spezi.core.internal

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import edu.stanford.spezi.core.SpeziApplication

/**
 * A [ContentProvider] that initializes the [SpeziApplication] when the application is created.
 *
 * This is used to ensure that the SpeziApplication is configured automatically before any other components
 * in the application.
 */
internal class SpeziApplicationContentProvider : ContentProvider() {
    private val logger by speziCoreLogger()

    override fun onCreate(): Boolean {
        logger.i { "Initializing SpeziApplicationContentProvider" }
        val application = context?.applicationContext as? SpeziApplication
        if (application != null) {
            logger.i { "Spezi application available. Configuring Spezi" }
            SpeziApplication.configure(application = application)
        } else {
            logger.w { "Spezi application not available. Skipping configuration for context: ${context?.packageName ?: "null"}" }
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0
}
