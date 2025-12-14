package edu.stanford.spezi.health.internal

import edu.stanford.spezi.health.CollectionMode

/**
 * Internal data class representing the delivery settings for health data collection.
 *
 * @property collectionMode The mode of data collection (e.g., Automatic, Manual).
 * @property continueInBackground Indicates whether data collection should continue in the background.
 */
internal data class HealthDataCollectorDeliverySetting(
    val collectionMode: CollectionMode,
    val continueInBackground: Boolean,
)
