package edu.stanford.spezi.module.account.spezi

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.module.account.account.model.GenderIdentity
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.collections.AccountDetailsSerializer
import edu.stanford.spezi.module.account.account.value.keys.genderIdentity
import edu.stanford.spezi.module.account.account.value.keys.isNewUser
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
}
