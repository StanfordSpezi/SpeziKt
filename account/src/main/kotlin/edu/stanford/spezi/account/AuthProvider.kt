package edu.stanford.spezi.account

import edu.stanford.spezi.ui.ImageResource
import edu.stanford.spezi.ui.StringResource

/**
 * Represents an authentication provider via which a user can sign in in the application.
 */
interface AuthProvider {
    /**
     * The name of the sign in action, e.g. Sign in with Google
     */
    val actionName: StringResource

    /**
     * An optional icon to be displayed alongside the action name
     */
    val icon: ImageResource?
}
