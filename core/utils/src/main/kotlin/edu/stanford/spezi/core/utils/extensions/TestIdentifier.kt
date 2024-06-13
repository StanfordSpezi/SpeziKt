package edu.stanford.spezi.core.utils.extensions

import edu.stanford.spezi.core.utils.TestIdentifier

/**
 * Constructs the test tag of an enum as `EnumTypeName:EnumCaseName`
 */
fun TestIdentifier.tag(suffix: String? = null): String = "${javaClass.simpleName}:$name${suffix ?: ""}"
