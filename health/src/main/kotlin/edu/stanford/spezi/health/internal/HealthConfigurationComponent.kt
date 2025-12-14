package edu.stanford.spezi.health.internal

import edu.stanford.spezi.health.HealthConstraint
import edu.stanford.spezi.health.HealthDataAccessRequirements

/**
 * Internal interface representing a health configuration component.
 */
internal interface HealthConfigurationComponent {
    /**
     * The health data access requirements for this configuration component.
     */
    val dataAccessRequirements: HealthDataAccessRequirements

    /**
     * Configures the given [client] with this configuration component.
     *
     * @param client The [DefaultHealthClient] to configure.
     * @param standard The standard [HealthConstraint] to apply, if any.
     */
    suspend fun configure(client: DefaultHealthClient, standard: HealthConstraint?)
}
