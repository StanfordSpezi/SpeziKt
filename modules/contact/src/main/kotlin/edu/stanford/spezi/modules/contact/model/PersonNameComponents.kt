package edu.stanford.spezi.modules.contact.model

data class PersonNameComponents(
    val namePrefix: String? = null,
    val givenName: String? = null,
    val middleName: String? = null,
    val familyName: String? = null,
    val nameSuffix: String? = null,
    val nickname: String? = null,
) {
    fun formatted(): String {
        val components = listOfNotNull(
            namePrefix,
            givenName,
            nickname?.let { "\"$it\"" },
            middleName,
            familyName,
            nameSuffix
        )
        return components.joinToString(" ")
    }
}
