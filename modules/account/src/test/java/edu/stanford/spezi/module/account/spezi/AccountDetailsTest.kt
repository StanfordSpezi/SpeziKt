package edu.stanford.spezi.module.account.spezi

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.module.account.account.model.GenderIdentity
import edu.stanford.spezi.module.account.account.service.configuration.AccountServiceConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.SupportedAccountKeys
import edu.stanford.spezi.module.account.account.service.configuration.UserIdConfiguration
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountDetailsSerializer
import edu.stanford.spezi.module.account.account.value.keys.AccountServiceConfigurationDetailsKey
import edu.stanford.spezi.module.account.account.value.keys.accountId
import edu.stanford.spezi.module.account.account.value.keys.email
import edu.stanford.spezi.module.account.account.value.keys.genderIdentity
import edu.stanford.spezi.module.account.account.value.keys.isNewUser
import edu.stanford.spezi.module.account.account.value.keys.userId
import kotlinx.serialization.json.Json
import org.junit.Test

class AccountDetailsTest {

    @Test
    fun testCoding() {
        val details = mockAccountDetails()

        val serializer = AccountDetailsSerializer(keys = details.keys)
        val encoded = Json.encodeToString(serializer, details)
        val decoded = Json.decodeFromString(serializer, encoded)

        assertAccountDetailsEqual(details, decoded)
        assertThat(decoded.isNewUser).isFalse()
    }

    @Test
    fun testCodingWithCustomMapping() {
        val details = AccountDetails()
        details.genderIdentity = GenderIdentity.FEMALE

        val mapping = mapOf("GenderIdentityKey" to AccountKeys.genderIdentity)
        val serializer = AccountDetailsSerializer(
            listOf(AccountKeys.genderIdentity),
            mapping
        )

        val string = Json.encodeToString(serializer, details)
        assertThat(string).isEqualTo("{\"GenderIdentityKey\":\"female\"}")
        val decoded = Json.decodeFromString(serializer, string)

        assertThat(details.genderIdentity).isEqualTo(decoded.genderIdentity)
        assertThat(details.keys).isEqualTo(decoded.keys)
    }

    @Test
    fun testUserIdKeyFallback() {
        val details = AccountDetails()
        details.accountId = "Hello World"
        assertThat(details.userId).isEqualTo("Hello World")
    }

    @Test
    fun testEmailKey() {
        val details = AccountDetails()
        details.userId = "username@example.org"
        details[AccountServiceConfigurationDetailsKey] = AccountServiceConfiguration(
            supportedKeys = SupportedAccountKeys.Arbitrary,
            listOf(UserIdConfiguration.emailAddress)
        )
        details.email = "example@example.org"

        assertThat(details.email).isEqualTo("example@example.org")
        details.email = null
        assertThat(details.email).isEqualTo("username@example.org")

        val usernameDetails = AccountDetails()
        usernameDetails.userId = "username"
        usernameDetails[AccountServiceConfigurationDetailsKey] = AccountServiceConfiguration(
            supportedKeys = SupportedAccountKeys.Arbitrary,
            listOf(UserIdConfiguration.username),
        )
        assertThat(usernameDetails.email).isNull()
    }
}
