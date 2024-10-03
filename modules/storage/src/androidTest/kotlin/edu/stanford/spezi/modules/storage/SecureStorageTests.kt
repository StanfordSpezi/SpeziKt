package edu.stanford.spezi.modules.storage

import androidx.test.platform.app.InstrumentationRegistry
import edu.stanford.spezi.modules.storage.secure.Credentials
import edu.stanford.spezi.modules.storage.secure.SecureStorage
import edu.stanford.spezi.modules.storage.secure.SecureStorageItemTypes
import org.junit.Test
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher

class SecureStorageTests {

    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val secureStorage = SecureStorage(targetContext)

    @Test
    fun testDeleteAllCredentials() {
        val serverCredentials1 = Credentials("@Schmiedmayer", "SpeziInventor")
        secureStorage.store(serverCredentials1, "apple.com")

        val serverCredentials2 = Credentials("Stanford Spezi", "Paul")
        secureStorage.store(serverCredentials2)

        secureStorage.createKey("DeleteKeyTest")
        secureStorage.deleteAllCredentials(SecureStorageItemTypes.all)

        assert(secureStorage.retrieveAllCredentials("apple.com").isEmpty())
        assert(secureStorage.retrieveAllCredentials().isEmpty())
        assert(secureStorage.retrievePrivateKey("DeleteKeyTest") == null)
        assert(secureStorage.retrievePublicKey("DeleteKeyTest") == null)
    }

    @Test
    fun testCredentials() {
        secureStorage.deleteAllCredentials(SecureStorageItemTypes.credentials)

        val serverCredentials0 = Credentials("@PSchmiedmayer", "SpeziInventor")
        secureStorage.store(serverCredentials0)
        secureStorage.store(serverCredentials0) // Overwrite existing credentials

        val retrievedCredentials0 = secureStorage.retrieveCredentials("@PSchmiedmayer")
        assert(serverCredentials0.username == retrievedCredentials0?.username)
        assert(serverCredentials0.password == retrievedCredentials0?.password)

        val serverCredentials1 = Credentials("@Spezi", "Paul")
        secureStorage.updateCredentials("@PSchmiedmayer", newCredentials = serverCredentials1)

        val retrievedCredentials1 = secureStorage.retrieveCredentials("@Spezi")
        assert(serverCredentials1.username == retrievedCredentials1?.username)
        assert(serverCredentials1.password == retrievedCredentials1?.password)

        secureStorage.deleteCredentials("@Spezi")
        assert(secureStorage.retrieveCredentials("@Spezi") == null)
    }

    @Test
    fun testInternetCredentials() {
        secureStorage.deleteAllCredentials(SecureStorageItemTypes.credentials)

        val credentials0 = Credentials("@PSchmiedmayer", "SpeziInventor")
        secureStorage.store(credentials0, server = "twitter.com")
        secureStorage.store(credentials0, server = "twitter.com") // Overwrite existing credentials.

        val retrievedCredentials0 =
            secureStorage.retrieveCredentials("@PSchmiedmayer", "twitter.com")
        assert(credentials0.username == retrievedCredentials0?.username)
        assert(credentials0.password == retrievedCredentials0?.password)

        val credentials1 = Credentials("@Spezi", "Paul")
        secureStorage.updateCredentials(
            "@PSchmiedmayer",
            server = "twitter.com",
            newCredentials = credentials1,
            newServer = "stanford.edu"
        )

        val retrievedCredentials1 = secureStorage.retrieveCredentials("@Spezi", "stanford.edu")
        assert(credentials1.username == retrievedCredentials1?.username)
        assert(credentials1.password == retrievedCredentials1?.password)

        secureStorage.deleteCredentials("@Spezi", "stanford.edu")
        assert(secureStorage.retrieveCredentials("@Spezi", "stanford.edu") == null)
    }

    @Test
    fun testMultipleInternetCredentials() {
        secureStorage.deleteAllCredentials(SecureStorageItemTypes.credentials)

        val credentials0 = Credentials("Paul Schmiedmayer", "SpeziInventor")
        secureStorage.store(credentials0, "linkedin.com")

        val credentials1 = Credentials("Stanford Spezi", "Paul")
        secureStorage.store(credentials1, "linkedin.com")

        val retrievedCredentials = secureStorage.retrieveAllCredentials(server = "linkedin.com")
        assert(retrievedCredentials.size == 2)

        assert(retrievedCredentials.firstOrNull { it.username == credentials0.username }?.password == credentials0.password)
        assert(retrievedCredentials.firstOrNull { it.username == credentials1.username }?.password == credentials1.password)

        secureStorage.deleteCredentials("Paul Schmiedmayer", server = "linkedin.com")
        secureStorage.deleteCredentials("Stanford Spezi", server = "linkedin.com")

        val retrievedCredentialsEmpty = secureStorage.retrieveAllCredentials("linkedin.com")
        assert(retrievedCredentialsEmpty.isEmpty())
    }

    @Test
    fun testMultipleCredentials() {
        secureStorage.deleteAllCredentials(SecureStorageItemTypes.credentials)

        val credentials0 = Credentials("Paul Schmiedmayer", "SpeziInventor")
        secureStorage.store(credentials0)

        val credentials1 = Credentials("Stanford Spezi", "Paul")
        secureStorage.store(credentials1)

        val retrievedCredentials = secureStorage.retrieveAllCredentials()
        assert(retrievedCredentials.size == 2)

        assert(retrievedCredentials.firstOrNull { it.username == credentials0.username }?.password == credentials0.password)
        assert(retrievedCredentials.firstOrNull { it.username == credentials1.username }?.password == credentials1.password)

        secureStorage.deleteCredentials("Paul Schmiedmayer")
        secureStorage.deleteCredentials("Stanford Spezi")

        val retrievedCredentialsEmpty = secureStorage.retrieveAllCredentials()
        assert(retrievedCredentialsEmpty.isEmpty())
    }

    @Test
    fun testKeys() {
        secureStorage.deleteAllCredentials(SecureStorageItemTypes.keys)
        assert(secureStorage.retrievePublicKey("MyKey") == null)

        val keyPair = secureStorage.createKey("MyKey")

        val privateKey = keyPair.private
        assert(secureStorage.retrievePrivateKey("MyKey") == privateKey)

        val publicKey = keyPair.public
        assert(secureStorage.retrievePublicKey("MyKey") == publicKey)

        val plainText = "Spezi & Paul Schmiedmayer".toByteArray(StandardCharsets.UTF_8)
        println(plainText.toString(StandardCharsets.UTF_8))

        val encipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        encipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = encipher.doFinal(plainText)

        val decipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        decipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = decipher.doFinal(encryptedBytes)
        println(decryptedBytes.toString(StandardCharsets.UTF_8))

        assert(decryptedBytes.contentEquals(plainText))
    }
}
