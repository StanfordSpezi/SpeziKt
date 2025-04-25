package edu.stanford.spezi.foundation

import java.util.UUID

/**
 * Creates a [UUID] instance from the specified string representation.
 *
 * @param string The string representation of the UUID.
 * @return The [UUID] instance parsed from the string.
 * @throws IllegalArgumentException if the specified string does not conform to the string representation format.
 */
fun UUID(string: String): UUID = UUID.fromString(string)

/**
 * @return a random [UUID]
 */
fun UUID(): UUID = UUID.randomUUID()
