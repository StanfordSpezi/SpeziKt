package edu.stanford.spezi.foundation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ObjectIdentifierTest {
    private data class TestClass(val name: String)

    @Test
    fun `it should handle same object equality correctly`() {
        // given
        val speziInstance = TestClass("Spezi")
        val otherSpeziInstance = TestClass("Spezi")
        val apodiniInstance = TestClass("Apodini")
        val otherApodiniInstance = TestClass("Apodini")
        val spezi = ObjectIdentifier(speziInstance)
        val otherSpezi = ObjectIdentifier(otherSpeziInstance)
        val apodini = ObjectIdentifier(apodiniInstance)
        val otherApodini = ObjectIdentifier(otherApodiniInstance)

        // then
        assertThat(speziInstance).isEqualTo(otherSpeziInstance)
        assertThat(speziInstance).isEqualTo(spezi.ref)
        assertThat(apodiniInstance).isEqualTo(otherApodiniInstance)
        assertThat(apodiniInstance).isEqualTo(apodini.ref)
        assertThat(spezi).isEqualTo(ObjectIdentifier(speziInstance))
        assertThat(apodini).isEqualTo(ObjectIdentifier(apodiniInstance))
        assertThat(spezi).isNotEqualTo(apodini)
        assertThat(spezi).isNotEqualTo(otherSpezi)
        assertThat(apodini).isNotEqualTo(otherApodini)
    }
}
