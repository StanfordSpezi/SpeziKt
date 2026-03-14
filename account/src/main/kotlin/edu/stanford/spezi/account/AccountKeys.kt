package edu.stanford.spezi.account

import edu.stanford.spezi.account.AccountKeys.accountId
import edu.stanford.spezi.account.AccountKeys.userId
import edu.stanford.spezi.foundation.ComputedKnowledgeSource
import edu.stanford.spezi.foundation.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.foundation.OptionalComputedKnowledgeSource
import edu.stanford.spezi.foundation.ValueRepository
import edu.stanford.spezi.ui.StringResource
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlin.reflect.KClass

/**
 * An object holding all predefined account keys for easy reference.
 *
 * If needed you can extend this object with your own custom keys as well so that you have a central component for accessing
 * you keys, for example:
 *
 * ```kotlin
 * val AccountKeys.myCustomKey
 *     get() = MyCustomKey
 * ```
 */
object AccountKeys {

    /**
     * Reference to the [AccountIdKey]
     */
    val accountId = AccountIdKey

    /**
     * Reference to the [UserIdKey]
     */
    val userId = UserIdKey

    /**
     * Reference to the [EmailKey]
     */
    val email = EmailKey

    /**
     * Reference to the [NameKey]
     */
    val name = NameKey

    /**
     * Reference to the [PasswordKey]
     */
    val password = PasswordKey

    /**
     * Reference to the [GenderIdentityKey]
     */
    val genderIdentity = GenderIdentityKey
}

/**
 * The account id key, which is required for all accounts and must be supplied in the [AccountDetails] when signing in or signing up.
 * The value of this key is used by the [Account] to determine whether a user is signed in or not,
 * and to identify the user across app restarts.
 */
data object AccountIdKey : AccountKey<String> {
    override val identifier: String = "accountId"
    override val name: StringResource = StringResource("Account ID")
    override val serializer: KSerializer<String> = String.serializer()
    override val category: AccountKeyCategory = AccountKeyCategory.Credentials
    override val initialValue = InitialValue.string
    override val display: DataDisplayComposable<String>? = null
    override val entry: DataEntryComposable<String>? = null
    override val valueType: KClass<String> = String::class
}

/**
 * The user id key, which is an optional computed key that defaults to the value of [accountId] if not explicitly set.
 * This allows you to have a separate user id that can be used for internal purposes,
 * while still using the account id as the primary identifier for the user.
 */
data object UserIdKey : AccountKey<String>, ComputedKnowledgeSource<AccountAnchor, String> {
    override val storagePolicy: ComputedKnowledgeSourceStoragePolicy = ComputedKnowledgeSourceStoragePolicy.AlwaysCompute

    override val identifier: String = "userId"
    override val name: StringResource = StringResource("User ID")
    override val serializer: KSerializer<String> = String.serializer()
    override val initialValue: InitialValue<String> = InitialValue.string
    override val category: AccountKeyCategory = AccountKeyCategory.Credentials
    override val display: DataDisplayComposable<String>? = null
    override val entry: DataEntryComposable<String>? = null
    override val valueType: KClass<String> = String::class

    override fun compute(repository: ValueRepository<AccountAnchor>): String {
        val currentValue = repository.getOrNull(UserIdKey::class)
        if (currentValue != null) return currentValue
        val accountId = accountId
        return repository[accountId::class] ?: accountId.initialValue.value
    }
}

/**
 * The email key, which is an optional computed key that defaults to the value of [userId] if the user id type is configured as email,
 * and null otherwise.
 */
data object EmailKey : AccountKey<String>, OptionalComputedKnowledgeSource<AccountAnchor, String> {
    override val storagePolicy: ComputedKnowledgeSourceStoragePolicy = ComputedKnowledgeSourceStoragePolicy.AlwaysCompute

    override val identifier: String = "email"
    override val name: StringResource = StringResource("Email")
    override val serializer: KSerializer<String> = String.serializer()
    override val initialValue: InitialValue<String> = InitialValue.string
    override val category: AccountKeyCategory = AccountKeyCategory.ContactDetails
    override val display: DataDisplayComposable<String>? = null
    override val entry: DataEntryComposable<String>? = null
    override val valueType: KClass<String> = String::class

    override fun compute(repository: ValueRepository<AccountAnchor>): String? {
        val currentValue = repository.getOrNull(this::class)
        if (currentValue != null) return currentValue
        return if (repository.accountServiceConfiguration.userIdConfiguration.idType == UserIdType.Email) {
            repository.getOrInitialValue(userId)
        } else {
            null
        }
    }
}

/**
 * The name key, which is an optional key that can be used to store the user's name if needed.
 * This key is not required for all accounts and can be used at your discretion to store additional information about the user.
 */
data object NameKey : AccountKey<String> {
    override val identifier: String = "name"
    override val name: StringResource = StringResource("Name")
    override val serializer: KSerializer<String> = String.serializer()
    override val initialValue: InitialValue<String> = InitialValue.nullable()
    override val category: AccountKeyCategory = AccountKeyCategory.ContactDetails
    override val display: DataDisplayComposable<String>? = null
    override val entry: DataEntryComposable<String>? = null
    override val valueType: KClass<String> = String::class
}

/**
 * The password key, which is an optional key that can be used to store the user's password if needed.
 * This key is not required for all accounts and should be used with caution, as storing passwords can have security implications.
 */
data object PasswordKey : AccountKey<String> {
    override val identifier: String = "password"
    override val name: StringResource = StringResource("Password")
    override val serializer: KSerializer<String> = String.serializer()
    override val initialValue: InitialValue<String> = InitialValue.nullable()
    override val category: AccountKeyCategory = AccountKeyCategory.Credentials
    override val display: DataDisplayComposable<String>? = null
    override val entry: DataEntryComposable<String>? = null
    override val valueType: KClass<String> = String::class
}

/**
 * The gender identity key, which is an optional key that can be used to store the user's gender
 */
data object GenderIdentityKey : AccountKey<GenderIdentity> {
    override val identifier: String = "genderIdentity"
    override val name: StringResource = StringResource("Gender")
    override val serializer: KSerializer<GenderIdentity> = GenderIdentity.serializer()
    override val initialValue: InitialValue<GenderIdentity> = InitialValue.default(GenderIdentity.PREFER_NOT_TO_STATE)
    override val category: AccountKeyCategory = AccountKeyCategory.PersonalDetails
    override val display: DataDisplayComposable<GenderIdentity>? = null
    override val entry: DataEntryComposable<GenderIdentity>? = null
    override val valueType: KClass<GenderIdentity> = GenderIdentity::class
}
