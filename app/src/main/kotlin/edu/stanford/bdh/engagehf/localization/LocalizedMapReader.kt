package edu.stanford.bdh.engagehf.localization

import edu.stanford.spezi.modules.utils.JsonMap
import edu.stanford.spezi.modules.utils.LocaleProvider
import javax.inject.Inject

class LocalizedMapReader @Inject constructor(
    private val localeProvider: LocaleProvider,
) {

    fun get(key: String, jsonMap: JsonMap?): String? {
        val fieldContent = jsonMap?.get(key)
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
}
