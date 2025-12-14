package edu.stanford.spezi.module.account.utils

import edu.stanford.spezi.core.design.views.personalinfo.PersonNameComponents
import edu.stanford.spezi.module.account.account.model.GenderIdentity
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.value.keys.dateOfBirth
import edu.stanford.spezi.module.account.account.value.keys.genderIdentity
import edu.stanford.spezi.module.account.account.value.keys.name
import edu.stanford.spezi.module.account.account.value.keys.password
import edu.stanford.spezi.module.account.account.value.keys.userId
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date

fun defaultAccountDetails() = AccountDetails().apply {
    userId = "lelandstanford@stanford.edu"
    password = "StanfordRocks123!"
    name = PersonNameComponents(givenName = "Leland", familyName = "Stanford")
    genderIdentity = GenderIdentity.MALE
    dateOfBirth = @Suppress("detekt:MagicNumber") Date.from(
        LocalDate.of(1824, 3, 9)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
    )
}
