package edu.stanford.spezi.core.design.views.personalinfo

data class PersonNameComponents(
    val namePrefix: String? = null,
    val givenName: String? = null,
    val middleName: String? = null,
    val familyName: String? = null,
    val nameSuffix: String? = null,
    val nickname: String? = null,
) {
    constructor(builder: Builder) : this(
        namePrefix = builder.namePrefix,
        givenName = builder.givenName,
        middleName = builder.middleName,
        familyName = builder.familyName,
        nameSuffix = builder.nameSuffix,
        nickname = builder.nickname,
    )

    enum class FormatStyle {
        // TODO: Styles SHORT and MEDIUM missing
        ABBREVIATED, LONG
    }

    fun createBuilder() = Builder(this)

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
            FormatStyle.ABBREVIATED -> listOfNotNull(
                givenName,
                familyName,
            ).joinToString("")
                .filter { it.isUpperCase() }
        }
    }

    class Builder(
        var namePrefix: String? = null,
        var givenName: String? = null,
        var middleName: String? = null,
        var familyName: String? = null,
        var nameSuffix: String? = null,
        var nickname: String? = null,
    ) {
        constructor(components: PersonNameComponents) : this(
            namePrefix = components.namePrefix,
            givenName = components.givenName,
            middleName = components.middleName,
            familyName = components.familyName,
            nameSuffix = components.nameSuffix,
            nickname = components.nickname,
        )

        fun build() = PersonNameComponents(this)
    }
}
