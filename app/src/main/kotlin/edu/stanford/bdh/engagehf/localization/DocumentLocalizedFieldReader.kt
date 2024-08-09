package edu.stanford.bdh.engagehf.localization

import com.google.firebase.firestore.DocumentSnapshot
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import edu.stanford.spezi.core.utils.LocaleProvider

class DocumentLocalizedFieldReader @AssistedInject constructor(
    @Assisted private val document: DocumentSnapshot,
    private val localeProvider: LocaleProvider,
) {

    fun get(field: String): String? {
        val fieldContent = document.get(field)
        return if (fieldContent is Map<*, *>) {
            val locale = localeProvider.getDefaultLocale()
            val language = locale.language // e.g. "en"
            val country = locale.country.uppercase() // e.g. "US"
            (fieldContent["$language-$country"] // en-US
                ?: fieldContent[language] // en
                ?: fieldContent["en"]
                ?: fieldContent.values.firstOrNull())
        } else {
            fieldContent
        } as? String
    }

    @AssistedFactory
    interface Factory {
        fun create(document: DocumentSnapshot): DocumentLocalizedFieldReader
    }
}
