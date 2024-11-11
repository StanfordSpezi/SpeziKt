package edu.stanford.spezi.core.design.views.personalInfo

data class PersonNameComponents(
    var namePrefix: String? = null,
    var givenName: String? = null,
    var middleName: String? = null,
    var familyName: String? = null,
    var nameSuffix: String? = null,
    var nickname: String? = null,
) {
    enum class FormatStyle {
        ABBREVIATED, SHORT, MEDIUM, LONG
    }

    fun formatted(style: edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents.FormatStyle = edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents.FormatStyle.LONG): String {
        return when (style) {
            edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents.FormatStyle.LONG -> listOfNotNull(
                namePrefix,
                givenName,
                nickname?.let { "\"$it\"" },
                middleName,
                familyName,
                nameSuffix
            ).joinToString(" ")
            edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents.FormatStyle.MEDIUM ->
                TODO("Not yet implemented.")
            edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents.FormatStyle.SHORT ->
                TODO("Not yet implemented.")
            edu.stanford.spezi.core.design.views.personalInfo.PersonNameComponents.FormatStyle.ABBREVIATED -> listOfNotNull(
                givenName,
                middleName,
                familyName,
            ).joinToString("")
                .filter { it.isUpperCase() }
        }
    }
}
