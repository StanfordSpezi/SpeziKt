package edu.stanford.spezi.spezi.ui.helpers

/**
 * Constructs the test tag of an enum as `EnumTypeName:EnumCaseName`
 */
fun TestIdentifier.tag(suffix: String? = null): String = "${javaClass.simpleName}:$name${suffix ?: ""}"
