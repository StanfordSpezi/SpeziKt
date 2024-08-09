package edu.stanford.bdh.engagehf.localization

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.spezi.core.utils.LocaleProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.util.Locale

class DocumentLocalizedFieldReaderTest {
    private val document: DocumentSnapshot = mockk()
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
    private val completeMap = hashMapOf(
        "random-value" to "random-value",
        "$localeLanguage-${localeCountry.uppercase()}" to languageCountryValue,
        localeLanguage to languageValue,
        "en" to englishValue,
    )

    private val reader = DocumentLocalizedFieldReader(
        document = document,
        localeProvider = localeProvider,
    )

    @Test
    fun `it should return null if field not available`() {
        // given
        every { document.get(title) } returns null

        // when
        val result = reader.get(title)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `it should return field string if field available`() {
        // given
        val value = "some-value"
        every { document.get(title) } returns value

        // when
        val result = reader.get(title)

        // then
        assertThat(result).isEqualTo(value)
    }

    @Test
    fun `it should return language country value if available`() {
        // given
        every { document.get(title) } returns completeMap

        // when
        val result = reader.get(title)

        // then
        assertThat(result).isEqualTo(languageCountryValue)
    }

    @Test
    fun `it should return language value if available`() {
        // given
        every { locale.country } returns "unknown-country"
        every { document.get(title) } returns completeMap

        // when
        val result = reader.get(title)

        // then
        assertThat(result).isEqualTo(languageValue)
    }

    @Test
    fun `it should return english value if available`() {
        // given
        every { locale.language } returns "unknown-language"
        every { locale.country } returns "unknown-country"
        every { document.get(title) } returns completeMap

        // when
        val result = reader.get(title)

        // then
        assertThat(result).isEqualTo(englishValue)
    }

    @Test
    fun `it should return first value if other values not available available`() {
        // given
        every { locale.language } returns "unknown-language"
        every { locale.country } returns "unknown-country"
        completeMap.remove("en")
        every { document.get(title) } returns completeMap
        val firstValue = completeMap.values.firstOrNull()

        // when
        val result = reader.get(title)

        // then
        assertThat(result).isEqualTo(firstValue)
    }
}
