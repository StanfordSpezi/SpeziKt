package edu.stanford.spezikt.spezi_module.contact

import edu.stanford.spezikt.spezi_module.contact.model.ContactOptionType
import edu.stanford.spezikt.spezi_module.contact.repository.DefaultContactRepository
import org.junit.Assert.assertEquals
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
            assertEquals(name, "Leland Stanford")
            assertEquals(title, "CEO")
            assertEquals(
                description,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            )
            assertEquals(organization, "Stanford University")
            assertEquals(address, "450 Jane Stanford Way Stanford, CA")
            assertEquals(options.size, 3)
            assertEquals(options[0].name, "Call")
            assertEquals(options[0].value, "+49 123 456 789")
            assertEquals(options[0].optionType, ContactOptionType.CALL)
        }
    }
}