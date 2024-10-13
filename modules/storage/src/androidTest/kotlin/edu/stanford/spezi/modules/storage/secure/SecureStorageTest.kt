package edu.stanford.spezi.modules.storage.secure

import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import edu.stanford.spezi.core.utils.UUID
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class SecureStorageTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var secureStorage: SecureStorage

    private val serverCredential = Credentials(
        username = "@Schmiedmayer",
        password = "top-secret",
        server = "apple.com",
    )

    private val nonServerCredential = Credentials(
        username = "@Spezi",
        password = "123456",
        server = null,
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        secureStorage.deleteAllCredentials(SecureStorageItemTypes.all)
    }

    @Test
    fun `it should store server credentials correctly`() {
        // given
        secureStorage.store(serverCredential)

        // when
        val serverCredentials = secureStorage.retrieveServerCredentials(server = serverCredential.server)
        val userServerCredential = secureStorage.retrieveCredentials(
            username = serverCredential.username,
            server = serverCredential.server,
        )
        val userCredentials = secureStorage.retrieveUserCredentials(serverCredential.username)

        // then
        assertThat(serverCredentials).containsExactly(serverCredential)
        assertThat(userServerCredential).isEqualTo(serverCredential)
        assertThat(userCredentials).containsExactly(serverCredential)
    }

    @Test
    fun `it should store non server credentials correctly`() {
        // given
        secureStorage.store(nonServerCredential)

        // when
        val serverCredentials = secureStorage.retrieveServerCredentials(server = nonServerCredential.server)
        val userServerCredential = secureStorage.retrieveCredentials(
            username = nonServerCredential.username,
            server = nonServerCredential.server,
        )
        val userCredentials = secureStorage.retrieveUserCredentials(nonServerCredential.username)

        // then
        assertThat(serverCredentials).containsExactly(nonServerCredential)
        assertThat(userCredentials).containsExactly(nonServerCredential)
        assertThat(userServerCredential).isEqualTo(nonServerCredential)
    }

    @Test
    fun `it should retrieve all user credentials correctly`() {
        // given
        val user = "@SpeziUser"
        val credentials = List(10) {
            serverCredential.copy(username = user, server = "com.server.$it")
        }
        credentials.forEach { secureStorage.store(it) }

        // when
        val storedCredentials = secureStorage.retrieveUserCredentials(user)

        // then
        assertThat(storedCredentials).containsExactlyElementsIn(credentials)
        assertThat(storedCredentials.all { it.username == user }).isTrue()
    }

    @Test
    fun `it should retrieve all server credentials correctly`() {
        // given
        val server = "edu.stanford.spezi"
        val credentials = List(10) { serverCredential.copy(username = "User$it", server = server) }
        credentials.forEach { secureStorage.store(it) }

        // when
        val storedCredentials = secureStorage.retrieveServerCredentials(server)

        // then
        assertThat(storedCredentials).containsExactlyElementsIn(credentials)
        assertThat(storedCredentials.all { it.server == server }).isTrue()
    }

    @Test
    fun `it should update credentials correctly`() {
        // given
        val updatedUserName = serverCredential.username + "- @Spezi"
        val newPassword = serverCredential.password.plus(UUID().toString())
        secureStorage.store(serverCredential)
        val updatedCredential = serverCredential.copy(
            username = updatedUserName,
            password = newPassword,
        )
        secureStorage.updateCredentials(
            username = serverCredential.username,
            server = serverCredential.server,
            newCredentials = updatedCredential
        )

        // when
        val oldCredential = secureStorage.retrieveCredentials(
            username = serverCredential.username,
            server = serverCredential.server,
        )
        val newCredential = secureStorage.retrieveCredentials(
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
        secureStorage.store(serverCredential)
        val beforeDeleteCredential = secureStorage.retrieveCredentials(
            username = serverCredential.username,
            server = serverCredential.server,
        )

        // when
        secureStorage.deleteCredentials(
            username = serverCredential.username,
            server = serverCredential.server,
        )
        val afterDeleteCredential = secureStorage.retrieveCredentials(
            username = serverCredential.username,
            server = serverCredential.server,
        )

        // then
        assertThat(beforeDeleteCredential).isEqualTo(serverCredential)
        assertThat(afterDeleteCredential).isNull()
    }

    @Test
    fun `it should delete all user credentials correctly`() {
        // given
        val user = "@SpeziUser"
        val credentials = List(10) {
            serverCredential.copy(username = user, server = "com.server.$it")
        }
        credentials.forEach { secureStorage.store(it) }
        val beforeDeleteCredentials = secureStorage.retrieveUserCredentials(user)

        // when
        secureStorage.deleteUserCredentials(user)
        val afterDeleteCredentials = secureStorage.retrieveUserCredentials(user)

        // then
        assertThat(beforeDeleteCredentials).containsExactlyElementsIn(credentials)
        assertThat(afterDeleteCredentials).isEmpty()
    }

    @Test
    fun `it should delete all server credentials correctly`() {
        // given
        val server = "com.apple"
        val credentials = List(10) {
            serverCredential.copy(username = "User $it", server = server)
        }
        credentials.forEach { secureStorage.store(it) }
        val beforeDeleteCredentials = secureStorage.retrieveServerCredentials(server)

        // when
        secureStorage.deleteServerCredentials(server)
        val afterDeleteCredentials = secureStorage.retrieveServerCredentials(server)

        // then
        assertThat(beforeDeleteCredentials).containsExactlyElementsIn(credentials)
        assertThat(afterDeleteCredentials).isEmpty()
    }

    @Test
    fun `it should handle deleting all server credentials correctly`() {
        // given
        listOf(serverCredential, nonServerCredential).forEach { secureStorage.store(it) }

        // when
        secureStorage.deleteAllCredentials(SecureStorageItemTypes.serverCredentials)
        val storedServerCredential = secureStorage.retrieveUserCredentials(
            username = serverCredential.username,
        )
        val storedNonServerCredential = secureStorage.retrieveUserCredentials(
            username = nonServerCredential.username,
        )

        // then
        assertThat(storedServerCredential).isEmpty()
        assertThat(storedNonServerCredential).containsExactly(nonServerCredential)
    }

    @Test
    fun `it should handle deleting non server credentials correctly`() {
        // given
        listOf(serverCredential, nonServerCredential).forEach { secureStorage.store(it) }

        // when
        secureStorage.deleteAllCredentials(SecureStorageItemTypes.nonServerCredentials)
        val storedServerCredential = secureStorage.retrieveUserCredentials(
            username = serverCredential.username,
        )
        val storedNonServerCredential = secureStorage.retrieveUserCredentials(
            username = nonServerCredential.username,
        )

        // then
        assertThat(storedServerCredential).containsExactly(serverCredential)
        assertThat(storedNonServerCredential).isEmpty()
    }

    @Test
    fun `it should handle deleting all credentials correctly`() {
        // given
        listOf(serverCredential, nonServerCredential).forEach { secureStorage.store(it) }

        // when
        secureStorage.deleteAllCredentials(SecureStorageItemTypes.all)
        val storedServerCredential = secureStorage.retrieveUserCredentials(
            username = serverCredential.username,
        )
        val storedNonServerCredential = secureStorage.retrieveUserCredentials(
            username = nonServerCredential.username,
        )

        // then
        assertThat(storedServerCredential).isEmpty()
        assertThat(storedNonServerCredential).isEmpty()
    }
}
