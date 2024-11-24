package edu.stanford.spezi.module.account.account.value.keys

import androidx.compose.runtime.Composable
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.validation.views.VerifiableTextField
import edu.stanford.spezi.core.utils.foundation.SharedRepository
import edu.stanford.spezi.core.utils.foundation.knowledgesource.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.module.account.account.service.configuration.UserIdType
import edu.stanford.spezi.module.account.account.service.configuration.userIdConfiguration
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.OptionalComputedAccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import edu.stanford.spezi.module.account.account.views.display.StringDisplay
import kotlinx.serialization.builtins.serializer

private object AccountEmailKey : OptionalComputedAccountKey<String> {
    override val identifier = "email"
    override val name = StringResource("USER_ID_EMAIL")
    override val category = AccountKeyCategory.personalDetails
    override val storagePolicy: ComputedKnowledgeSourceStoragePolicy
        get() = ComputedKnowledgeSourceStoragePolicy.AlwaysCompute
    override val initialValue: InitialValue<String> = InitialValue.Empty("")
    override val serializer = String.serializer()

    @Composable
    override fun DisplayComposable(value: String) {
        StringDisplay(this, value)
    }

    @Composable
    override fun EntryComposable(value: String, onValueChanged: (String) -> Unit) {
        VerifiableTextField(
            name.text(),
            value = value,
            onValueChanged = onValueChanged,
        )
        // TODO: Set content type, disable field assistants
    }

    override fun compute(repository: SharedRepository<AccountAnchor>): String? {
        return repository[this] ?: run {
            val idType = repository[AccountServiceConfigurationDetailsKey].userIdConfiguration.idType
            if (idType == UserIdType.EmailAddress) {
                repository[AccountKeys.userId]
            } else {
                null
            }
        }
    }
}

val AccountKeys.email: AccountKey<String>
    get() = AccountEmailKey

var AccountDetails.email: String?
    get() = this.storage[AccountKeys.email]
    set(value) { this.storage[AccountKeys.email] = value }
