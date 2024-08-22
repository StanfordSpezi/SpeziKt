package edu.stanford.bdh.engagehf.localization

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.utils.LocaleProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.util.Locale

class LocalizedMapReaderTest {
    private val localeLanguage = "language"
    private val localeCountry = "country"
    private val locale: Locale = mockk {
        every { language } returns localeLanguage
        every { country } returns localeCountry
    }
    private val localeProvider: LocaleProvider = mockk {
        every { getDefaultLocale() } returns locale
    }
    private val title = "some-title"
    private val languageCountryValue = "language-country-value"
    private val languageValue = "language-value"
    private val englishValue = "english-value"
    private val titleValuesMap by lazy {
        hashMapOf(
            "random-value" to "random-value",
            "$localeLanguage-${localeCountry.uppercase()}" to languageCountryValue,
            localeLanguage to languageValue,
            "en" to englishValue,
        )
    }
    private val titleMap by lazy {
        mapOf(title to titleValuesMap)
    }

    private val reader = LocalizedMapReader(
        localeProvider = localeProvider,
    )

    @Test
    fun `it should return null if field not available`() {
        // when
        val result = reader.get(key = title, jsonMap = emptyMap<String, String>())

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `it should return field string if field available`() {
        // given
        val value = "some-value"
        val map = mapOf(
            title to value
        )

        // when
        val result = reader.get(key = title, jsonMap = map)

        // then
        assertThat(result).isEqualTo(value)
    }

    @Test
    fun `it should return language country value if available`() {
        // when
        val result = reader.get(key = title, jsonMap = titleMap)

        // then
        assertThat(result).isEqualTo(languageCountryValue)
    }

    @Test
    fun `it should return language value if available`() {
        // given
        every { locale.country } returns "unknown-country"

        // when
        val result = reader.get(key = title, jsonMap = titleMap)

        // then
        assertThat(result).isEqualTo(languageValue)
    }

    @Test
    fun `it should return english value if available`() {
        // given
        every { locale.language } returns "unknown-language"
        every { locale.country } returns "unknown-country"

        // when
        val result = reader.get(key = title, jsonMap = titleMap)

        // then
        assertThat(result).isEqualTo(englishValue)
    }

    @Test
    fun `it should return first value if other values not available available`() {
        // given
        every { locale.language } returns "unknown-language"
        every { locale.country } returns "unknown-country"
        titleValuesMap.remove("en")
        val firstValue = titleValuesMap.values.firstOrNull()

        // when
        val result = reader.get(key = title, jsonMap = titleMap)

        // then
        assertThat(result).isEqualTo(firstValue)
    }
}
