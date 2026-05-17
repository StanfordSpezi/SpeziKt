package edu.stanford.spezi.account

import edu.stanford.spezi.account.AccountKeys.accountId
import edu.stanford.spezi.account.AccountKeys.userId
import edu.stanford.spezi.account.internal.screen.PersonNameDataEntry
import edu.stanford.spezi.foundation.ComputedKnowledgeSource
import edu.stanford.spezi.foundation.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.foundation.OptionalComputedKnowledgeSource
import edu.stanford.spezi.foundation.ValueRepository
import edu.stanford.spezi.resources.Strings
import edu.stanford.spezi.ui.ChoicesFormFieldItem
import edu.stanford.spezi.ui.StringResource
import edu.stanford.spezi.ui.account.ChoicesDataEntry
import edu.stanford.spezi.ui.account.DataDisplayComposable
import edu.stanford.spezi.ui.account.DataEntryComposable
import edu.stanford.spezi.ui.account.InstantDataEntry
import edu.stanford.spezi.ui.account.StringDataDisplay
import edu.stanford.spezi.ui.account.StringDataEntry
import edu.stanford.spezi.ui.account.ValueTextDisplay
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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

    /**
     * Reference to the [DateOfBirthKey]
     */
    val dateOfBirth = DateOfBirthKey
}

/**
 * The account id key, which is required for all accounts and must be supplied in the [AccountDetails] when signing in or signing up.
 * The value of this key is used by the [Account] to determine whether a user is signed in or not,
 * and to identify the user across app restarts.
 */
data object AccountIdKey : AccountKey<String> {
    override val identifier: String = "accountId"
    override val name: StringResource = StringResource(Strings.account_key_account_id)
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
    override val name: StringResource = StringResource(Strings.account_key_user_id)
    override val serializer: KSerializer<String> = String.serializer()
    override val initialValue: InitialValue<String> = InitialValue.string
    override val category: AccountKeyCategory = AccountKeyCategory.Credentials
    override val display: DataDisplayComposable<String> = StringDataDisplay()
    override val entry: DataEntryComposable<String> = StringDataEntry(placeholder = name)
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
    override val name: StringResource = StringResource(Strings.account_key_email)
    override val serializer: KSerializer<String> = String.serializer()
    override val initialValue: InitialValue<String> = InitialValue.string
    override val category: AccountKeyCategory = AccountKeyCategory.ContactDetails
    override val display: DataDisplayComposable<String> = StringDataDisplay()
    override val entry: DataEntryComposable<String> = StringDataEntry(placeholder = name)
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
data object NameKey : AccountKey<PersonName> {
    override val identifier: String = "name"
    override val name: StringResource = StringResource(Strings.account_key_name)
    override val serializer: KSerializer<PersonName> = PersonName.serializer()
    override val initialValue: InitialValue<PersonName> = InitialValue.Empty(value = PersonName(fullName = ""))
    override val category: AccountKeyCategory = AccountKeyCategory.Name
    override val display: DataDisplayComposable<PersonName> = ValueTextDisplay {
        StringResource(it.fullName)
    }
    override val entry: DataEntryComposable<PersonName> = PersonNameDataEntry()
    override val valueType: KClass<PersonName> = PersonName::class
}

/**
 * The password key, which is an optional key that can be used to store the user's password if needed.
 * This key is not required for all accounts and should be used with caution, as storing passwords can have security implications.
 */
data object PasswordKey : AccountKey<String> {
    override val identifier: String = "password"
    override val name: StringResource = StringResource(Strings.account_key_password)
    override val serializer: KSerializer<String> = String.serializer()
    override val initialValue: InitialValue<String> = InitialValue.string
    override val category: AccountKeyCategory = AccountKeyCategory.Credentials
    override val display: DataDisplayComposable<String>? = null
    override val entry: DataEntryComposable<String> = StringDataEntry(
        placeholder = name,
        hideContent = true,
    )
    override val valueType: KClass<String> = String::class
}

/**
 * The gender identity key, which is an optional key that can be used to store the user's gender
 */
data object GenderIdentityKey : AccountKey<GenderIdentity> {
    override val identifier: String = "genderIdentity"
    override val name: StringResource = StringResource(Strings.account_key_gender)
    override val serializer: KSerializer<GenderIdentity> = GenderIdentity.serializer()
    override val initialValue: InitialValue<GenderIdentity> = InitialValue.default(GenderIdentity.PREFER_NOT_TO_STATE)
    override val category: AccountKeyCategory = AccountKeyCategory.PersonalDetails
    override val display: DataDisplayComposable<GenderIdentity> = ValueTextDisplay { it.title }
    override val entry: DataEntryComposable<GenderIdentity> = ChoicesDataEntry(
        choices = GenderIdentity.entries,
        optionTransformer = { identity ->
            ChoicesFormFieldItem.Option(id = identity.name, label = identity.title)
        },
        valueTransformer = { id -> GenderIdentity.valueOf(id) },
    )
    override val valueType: KClass<GenderIdentity> = GenderIdentity::class
}

/**
 * The date of birth key, storing the user's birthdate as a [java.time.Instant].
 */
data object DateOfBirthKey : AccountKey<Instant> {
    private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

    override val identifier: String = "dateOfBirth"
    override val name: StringResource = StringResource(Strings.account_key_date_of_birth)
    override val serializer: KSerializer<Instant> = object : KSerializer<Instant> {
        override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.LONG)
        override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeLong(value.toEpochMilli())
        override fun deserialize(decoder: Decoder): Instant = Instant.ofEpochMilli(decoder.decodeLong())
    }
    override val initialValue: InitialValue<Instant> = InitialValue.empty(Instant.MIN)
    override val category: AccountKeyCategory = AccountKeyCategory.PersonalDetails
    override val display: DataDisplayComposable<Instant> = ValueTextDisplay { format(it) }
    override val entry: DataEntryComposable<Instant> = InstantDataEntry(
        placeholder = StringResource(Strings.account_key_date_of_birth_placeholder),
        formatter = ::format,
    )
    override val valueType: KClass<Instant> = Instant::class

    private fun format(instant: Instant): StringResource {
        return if (instant == initialValue.value) {
            StringResource(Strings.account_key_date_of_birth_unspecified)
        } else {
            StringResource(dateFormatter.format(instant.atZone(ZoneId.systemDefault()).toLocalDate()))
        }
    }
}
