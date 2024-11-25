package edu.stanford.spezi.module.account.account.model

import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.module.account.account.views.display.StringResourceConvertible

enum class GenderIdentity : StringResourceConvertible {
    FEMALE, MALE, TRANSGENDER, NON_BINARY, PREFER_NOT_TO_STATE;

    override val stringResource: StringResource get() = when (this) {
        FEMALE -> StringResource("Female")
        MALE -> StringResource("Male")
        NON_BINARY -> StringResource("Non-binary")
        PREFER_NOT_TO_STATE -> StringResource("Prefer not to state")
        TRANSGENDER -> StringResource("Transgender")
    }
}
