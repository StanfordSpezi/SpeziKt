package edu.stanford.spezi.module.account.account.value.keys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.utils.foundation.SharedRepository
import edu.stanford.spezi.core.utils.foundation.knowledgesource.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.OptionalComputedAccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails

private object AccountEmailKey : OptionalComputedAccountKey<String> {
    override val identifier = "email"
    override val name = StringResource("UAP_SIGNUP_DATE_OF_BIRTH_TITLE")
    override val category = AccountKeyCategory.personalDetails
    override val storagePolicy: ComputedKnowledgeSourceStoragePolicy
        get() = ComputedKnowledgeSourceStoragePolicy.AlwaysCompute
    override val initialValue: InitialValue<String> = InitialValue.Empty("")

    @Composable
    override fun DisplayComposable(value: String) {
        TODO("Not yet implemented")
    }

    @Composable
    override fun EntryComposable(state: MutableState<String>) {
        TODO("Not yet implemented")
    }

    override fun compute(repository: SharedRepository<AccountAnchor>): String? {
        repository[this]?.let {
            return it
        } ?: TODO("""
        guard let configuration = repository[AccountDetails.AccountServiceConfigurationDetailsKey.self],
        case .emailAddress = configuration.userIdConfiguration.idType else {
            return nil
        }

        // return the userId if it's a email address
        return repository[AccountKeys.userId]
        """.trimIndent()
        )
    }
}

val AccountKeys.email: AccountKey<String>
    get() = AccountEmailKey

var AccountDetails.email: String?
    get() = this.storage[AccountKeys.email]
    set(value) { this.storage[AccountKeys.email] = value }
