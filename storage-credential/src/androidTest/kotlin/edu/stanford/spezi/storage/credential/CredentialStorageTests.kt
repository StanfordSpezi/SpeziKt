package edu.stanford.spezi.storage.credential

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.foundation.UUID
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class CredentialStorageTests {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var credentialStorage: CredentialStorage

    private val serverCredential = Credential(
        username = "@Schmiedmayer",
        password = "top-secret",
        server = "apple.com"
    )

    private val nonServerCredential = Credential(
        username = "@Spezi",
        password = "123456",
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        credentialStorage.deleteAll(CredentialTypes.All)
    }

    @Test
    fun `it should store server credentials correctly`() {
        // given
        credentialStorage.store(serverCredential)

        // when
        val serverCredentials = credentialStorage.retrieveAll(server = serverCredential.server!!)
        val userServerCredential = credentialStorage.retrieve(
            username = serverCredential.username,
            server = serverCredential.server,
        )

        // then
        assertThat(serverCredentials).containsExactly(serverCredential)
        assertThat(userServerCredential).isEqualTo(serverCredential)
    }

    @Test
    fun `it should store non server credentials correctly`() {
        // given
        credentialStorage.store(nonServerCredential)

        // when
        val userServerCredential = credentialStorage.retrieve(
            username = nonServerCredential.username
        )
        val userCredentials = credentialStorage.retrieve(nonServerCredential.username)

        // then
        assertThat(userCredentials).isEqualTo(nonServerCredential)
        assertThat(userServerCredential).isEqualTo(nonServerCredential)
    }

    @Test
    fun `it should retrieve all server credentials correctly`() {
        // given
        val server = "edu.stanford.spezi"
        val credentials = List(10) { serverCredential.copy(username = "User$it", server = server) }
        credentials.forEach { credentialStorage.store(it) }

        // when
        val storedCredentials = credentialStorage.retrieveAll(server)

        // then
        assertThat(storedCredentials).containsExactlyElementsIn(credentials)
        assertThat(storedCredentials.all { it.server == server }).isTrue()
    }

    @Test
    fun `it should update credentials correctly`() {
        // given
        val updatedUserName = serverCredential.username + "- @Spezi"
        val newPassword = serverCredential.password.plus(UUID().toString())
        credentialStorage.store(serverCredential)
        val updatedCredential = serverCredential.copy(
            username = updatedUserName,
            password = newPassword,
        )
        credentialStorage.update(
            username = serverCredential.username,
            server = serverCredential.server,
            newCredential = updatedCredential,
        )

        // when
        val oldCredential = credentialStorage.retrieve(
            username = serverCredential.username,
            server = serverCredential.server,
        )
        val newCredential = credentialStorage.retrieve(
            username = updatedCredential.username,
            server = updatedCredential.server,
        )

        // then
        assertThat(oldCredential).isNull()
        assertThat(newCredential).isEqualTo(updatedCredential)
    }

    @Test
    fun `it should delete credentials correctly`() {
        // given
        credentialStorage.store(serverCredential)
        val beforeDeleteCredential = credentialStorage.retrieve(
            username = serverCredential.username,
            server = "apple.com",
        )

        // when
        credentialStorage.delete(
            username = serverCredential.username,
            server = serverCredential.server,
        )
        val afterDeleteCredential = credentialStorage.retrieve(
            username = serverCredential.username,
            server = serverCredential.server,
        )

        // then
        assertThat(beforeDeleteCredential).isEqualTo(serverCredential)
        assertThat(afterDeleteCredential).isNull()
    }

    @Test
    fun `it should handle deleting all server credentials correctly`() {
        // given
        listOf(serverCredential, nonServerCredential).forEach { credentialStorage.store(it) }

        // when
        credentialStorage.deleteAll(CredentialTypes.Server)
        val storedServerCredential = credentialStorage.retrieve(
            username = serverCredential.username,
        )
        val storedNonServerCredential = credentialStorage.retrieve(
            username = nonServerCredential.username,
        )

        // then
        assertThat(storedServerCredential).isNull()
        assertThat(storedNonServerCredential).isEqualTo(nonServerCredential)
    }

    @Test
    fun `it should handle deleting non server credentials correctly`() {
        // given
        listOf(serverCredential, nonServerCredential).forEach { credentialStorage.store(it) }

        // when
        credentialStorage.deleteAll(CredentialTypes.NonServer)
        val storedServerCredential = credentialStorage.retrieve(
            username = serverCredential.username,
            server = serverCredential.server,
        )
        val storedNonServerCredential = credentialStorage.retrieve(
            username = nonServerCredential.username,
        )

        // then
        assertThat(storedServerCredential).isEqualTo(serverCredential)
        assertThat(storedNonServerCredential).isNull()
    }

    @Test
    fun `it should handle deleting all credentials correctly`() {
        // given
        listOf(serverCredential, nonServerCredential).forEach { credentialStorage.store(it) }

        // when
        credentialStorage.deleteAll(CredentialTypes.All)
        val storedServerCredential = credentialStorage.retrieve(
            username = serverCredential.username,
        )
        val storedNonServerCredential = credentialStorage.retrieve(
            username = nonServerCredential.username,
        )

        // then
        assertThat(storedServerCredential).isNull()
        assertThat(storedNonServerCredential).isNull()
    }
}
