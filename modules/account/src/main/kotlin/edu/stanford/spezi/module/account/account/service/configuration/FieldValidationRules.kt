package edu.stanford.spezi.module.account.account.service.configuration

import com.google.apphosting.datastore.testing.DatastoreTestTrace.ValidationRule
import edu.stanford.spezi.module.account.account.value.collections.AccountKey
import kotlin.reflect.KClass

data class FieldValidationRules<Key: AccountKey<String>> (
    val key: KClass<Key>,
    val rules: List<ValidationRule>,
): AccountServiceConfigurationKey<String> {
    companion object {
        inline fun <reified Key: AccountKey<String>> compute(
            storage: AccountServiceConfigurationStorage
        ): FieldValidationRules<Key>? {
            return storage[FieldValidationRules<Key>::class]
        }
    }
}