package edu.stanford.spezi.modules.contact.model

import android.location.Address

fun Address.formatted(): String {
    val lines = (0..maxAddressLineIndex).map { getAddressLine(it) }
    val areaLine = listOf(locality, adminArea, postalCode).mapNotNull { it }.joinToString(" ")
    val countryLine = countryName ?: ""
    return ((lines + areaLine) + countryLine)
        .filter { it.isNotBlank() }
        .joinToString("\n")
}
