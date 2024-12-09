package edu.stanford.spezi.module.account.spezi

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.module.account.account.model.GenderIdentity
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.accountId
import edu.stanford.spezi.module.account.account.value.keys.dateOfBirth
import edu.stanford.spezi.module.account.account.value.keys.genderIdentity
import edu.stanford.spezi.module.account.account.value.keys.isNewUser
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.password
import edu.stanford.spezi.module.account.account.value.keys.userId
import java.time.Instant
import java.util.Date
import java.util.UUID

fun mockAccountDetails(id: UUID = UUID.randomUUID(), date: Date = Date.from(Instant.now())) =
    AccountDetails().apply {
        accountId = id.toString()
        userId = "lelandstanford@stanford.edu"
        password = "12345678"
        name = PersonNameComponents(givenName = "Leland", familyName = "Stanford")
        dateOfBirth = date
        genderIdentity = GenderIdentity.MALE
        isNewUser = true
    }

fun assertAccountDetailsEqual(details1: AccountDetails, details2: AccountDetails) {
    assertThat(details1.accountId).isEqualTo(details2.accountId)
    assertThat(details1.userId).isEqualTo(details2.userId)
    assertThat(details1.password).isEqualTo(details2.password)
    assertThat(details1.name).isEqualTo(details2.name)
    assertThat(details1.dateOfBirth).isEqualTo(details2.dateOfBirth)
    assertThat(details1.genderIdentity).isEqualTo(details2.genderIdentity)
}
