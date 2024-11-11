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

    fun formatted(style: FormatStyle = FormatStyle.LONG): String {
        return when (style) {
            FormatStyle.LONG -> listOfNotNull(
                namePrefix,
                givenName,
                nickname?.let { "\"$it\"" },
                middleName,
                familyName,
                nameSuffix
            ).joinToString(" ")
            FormatStyle.MEDIUM ->
                TODO("Not yet implemented.")
            FormatStyle.SHORT ->
                TODO("Not yet implemented.")
            FormatStyle.ABBREVIATED -> listOfNotNull(
                givenName,
                familyName,
            ).joinToString("")
                .filter { it.isUpperCase() }
        }
    }
}
