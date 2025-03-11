package edu.stanford.spezi.modules.utils.extensions

import kotlin.math.pow

@Suppress("MagicNumber")
fun Double.roundToDecimalPlaces(places: Int): Double {
    val factor = (10.0).pow(places)
    return Math.round(this * factor) / factor
}
