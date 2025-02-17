package edu.stanford.bdh.heartbeat.app.fake

import edu.stanford.bdh.heartbeat.app.R

object FakeConfigs {
    /**
     * If [true] simutales a fake flow with example questions from [R.raw.fake_data]
     */
    const val ENABLED = true

    /**
     * Indicates whether a dummy login / register flow should be used, otherwise survey starts immediately
     */
    const val SKIP_LOGIN = true

    /**
     * Max value in seconds that simulates async request durations to visualize loading states
     */
    const val MAX_DELAY_SECONDS = 2L

    /**
     * Whether dummy account should have the email already verified
     */
    const val EMAIL_VERIFIED = false

    /**
     * Set to true to ignore question validations and complete the flow
     */
    const val FORCE_ENABLE_CONTINUE = false
}
