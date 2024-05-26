package edu.stanford.spezi.core.utils.extensions

/**
 * Extension function that returns a new list by appending an item (if not null) to itself.
 *
 * @param item The item to append to the list. If null, it will not be added.
 * @return A new list containing the original items and the appended item, if not null.
 */
fun <T> List<T>.append(item: T?): List<T> = this + listOfNotNull(item)
