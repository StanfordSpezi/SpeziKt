package edu.stanford.spezi.account

import kotlinx.serialization.Serializable

/**
 * A person's name, split into given and family name components.
 *
 * @param givenName The person's first name.
 * @param familyName The person's last name.
 */
@Serializable
data class PersonName(
    val givenName: String,
    val familyName: String,
) {
    val fullName = "$givenName $familyName"

    companion object {
        /**
         * Parses a flat display name into a [PersonName], splitting on the first space.
         *
         * @param fullName The full name string to parse.
         * @return A [PersonName] where everything before the first space is [givenName] and the rest is [familyName].
         */
        operator fun invoke(fullName: String): PersonName {
            val spaceIndex = fullName.indexOf(' ')
            return if (spaceIndex == -1) {
                PersonName(givenName = fullName, familyName = "")
            } else {
                PersonName(
                    givenName = fullName.substring(0, spaceIndex),
                    familyName = fullName.substring(spaceIndex + 1),
                )
            }
        }
    }
}
