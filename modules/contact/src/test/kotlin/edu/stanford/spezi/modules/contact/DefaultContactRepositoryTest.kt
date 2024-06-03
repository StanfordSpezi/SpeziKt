package edu.stanford.spezi.modules.contact

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.modules.contact.model.ContactOptionType
import edu.stanford.spezi.modules.contact.repository.DefaultContactRepository
import org.junit.Before
import org.junit.Test

class DefaultContactRepositoryTest {

    private lateinit var repository: DefaultContactRepository

    @Before
    fun setup() {
        repository = DefaultContactRepository()
    }

    @Test
    fun defaultContactRepository_loadsContact() {
        val contact = repository.getContact()
        with(contact) {
            assertThat(name).isEqualTo("Leland Stanford")
            assertThat(title).isEqualTo("CEO")
            assertThat(description).isEqualTo("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
            assertThat(organization).isEqualTo("Stanford University")
            assertThat(address).isEqualTo("450 Jane Stanford Way Stanford, CA")
            assertThat(options.size).isEqualTo(3)
            assertThat(options[0].name).isEqualTo("Call")
            assertThat(options[0].value).isEqualTo("+49 123 456 789")
            assertThat(options[0].optionType).isEqualTo(ContactOptionType.CALL)
        }
    }
}
