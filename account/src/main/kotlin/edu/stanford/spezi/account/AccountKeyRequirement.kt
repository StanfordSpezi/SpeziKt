package edu.stanford.spezi.account

/**
 * Describes the user-configured requirement level for an [AccountKey].
 *
 * This determines when and how a value is collected and displayed in account-related UI,
 * such as signup forms and account overview screens.
 */
enum class AccountKeyRequirement {

    /**
     * The associated account value **must** be provided by the user at signup.
     *
     * This value is mandatory and the signup flow cannot complete without it.
     *
     * Keys marked as [REQUIRED] are expected to be displayable and mutable,
     * and should provide a valid entry UI.
     */
    REQUIRED,

    /**
     * The associated account value **can** be provided by the user at signup.
     *
     * The value is collected during signup but is optional — the user may skip it.
     *
     * The value is still displayed and editable later in the account overview.
     */
    COLLECTED,

    /**
     * The associated account value **can** be provided by the user after signup.
     *
     * The value is not shown during signup, but it is displayed in the account overview
     * and can be edited later.
     */
    SUPPORTED,

    /**
     * The associated account value is handled manually by the application.
     *
     * It is not collected at signup and is not shown in the account overview.
     * This is useful for internal metadata or values managed outside of UI flows.
     */
    MANUAL,
}
