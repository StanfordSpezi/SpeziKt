package edu.stanford.spezi.utils.extensions

fun <T> List<T>.append(item: T?): List<T> = this + listOfNotNull(item)