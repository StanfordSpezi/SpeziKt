package edu.stanford.bdh.engagehf.contact.data

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.DocumentSnapshot
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class ContactDocumentToContactMapperTest {
    private val mapper = ContactDocumentToContactMapper()

    @Test
    fun `it should return null if contact name is missing`() {
        // given
        val document: DocumentSnapshot = mockk {
            every { getString("contactName") } returns null
        }

        // when
        val result = mapper.map(document)

        // then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `it should return null if organization name is missing`() {
        // given
        val document: DocumentSnapshot = mockk {
            every { getString("contactName") } returns "Leland Stanford"
            every { getString("name") } returns null
        }

        // when
        val result = mapper.map(document)

        // then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `it should return a valid Contact`() {
        // given
        val givenGivenName = "Leland"
        val givenFamilyName = "Stanford"
        val givenTitle = "University Founder"
        val givenOrganizationName = "Stanford University"
        val document: DocumentSnapshot = mockk {
            every { getString("contactName") } returns "$givenGivenName $givenFamilyName, $givenTitle"
            every { getString("name") } returns givenOrganizationName
            every { getString("emailAddress") } returns "test@gmail.com"
            every { getString("phoneNumber") } returns "+49 123 456 789"
        }

        // when
        val result = requireNotNull(mapper.map(document).getOrNull())

        // then
        with(result) {
            assertThat(name).isEqualTo(
                PersonNameComponents(
                    givenName = givenGivenName,
                    familyName = givenFamilyName,
                )
            )
            assertThat(title).isEqualTo(StringResource(givenTitle))
            assertThat(organization).isEqualTo(StringResource(givenOrganizationName))
            assertThat(options).hasSize(2)
        }
    }
}
