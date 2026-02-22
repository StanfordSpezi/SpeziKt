package edu.stanford.spezi.account

import edu.stanford.spezi.ui.StringResource
import kotlinx.serialization.Serializable

/**
 * An enum class representing the gender identity of an account holder.
 */
@Serializable
enum class GenderIdentity {
    /**
     * Represents a female gender identity.
     */
    FEMALE,

    /**
     * Represents a male gender identity.
     */
    MALE,

    /**
     * Represents a transgender gender identity.
     */
    TRANSGENDER,

    /**
     * Represents a non-binary gender identity.
     */
    NON_BINARY,

    /**
     * Represents a preference not to state the gender identity.
     */
    PREFER_NOT_TO_STATE,
    ;

    /**
     * Returns a [StringResource] representing the display name of the gender identity for UI purposes.
     */
    val title: StringResource
        get() {
            val resId = when (this) {
                FEMALE -> R.string.account_gender_identity_female
                MALE -> R.string.account_gender_identity_male
                TRANSGENDER -> R.string.account_gender_identity_transgender
                NON_BINARY -> R.string.account_gender_identity_non_binary
                PREFER_NOT_TO_STATE -> R.string.account_gender_identity_prefer_not_to_state
            }
            return StringResource.Companion(id = resId)
        }
}
