package edu.stanford.spezi.core.utils.extensions

import kotlin.math.pow

@Suppress("MagicNumber")
fun Float.roundToDecimalPlaces(places: Int): Float {
    val factor = (10.0).pow(places).toFloat()
    return Math.round(this * factor) / factor
}
