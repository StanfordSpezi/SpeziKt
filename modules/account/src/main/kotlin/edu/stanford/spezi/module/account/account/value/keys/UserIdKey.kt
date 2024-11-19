package edu.stanford.spezi.module.account.account.value.keys

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import edu.stanford.spezi.core.design.component.ListRow
import edu.stanford.spezi.core.design.component.StringResource
import edu.stanford.spezi.core.design.views.validation.views.VerifiableTextField
import edu.stanford.spezi.core.utils.foundation.SharedRepository
import edu.stanford.spezi.core.utils.foundation.knowledgesource.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.module.account.account.compositionLocal.LocalAccountServiceConfiguration
import edu.stanford.spezi.module.account.account.service.configuration.userIdConfiguration
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.AccountKeys
import edu.stanford.spezi.module.account.account.value.ComputedAccountKey
import edu.stanford.spezi.module.account.account.value.InitialValue
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails
import kotlinx.serialization.builtins.serializer

private object AccountUserIdKey : ComputedAccountKey<String> {
    override val identifier: String = "userId"
    override val name = StringResource("USER_ID")
    override val category = AccountKeyCategory.personalDetails
    override val storagePolicy: ComputedKnowledgeSourceStoragePolicy
        get() = ComputedKnowledgeSourceStoragePolicy.AlwaysCompute
    override val initialValue: InitialValue<String> = InitialValue.Empty("")
    override val serializer = String.serializer()

    @Composable
    override fun DisplayComposable(value: String) {
        val configuration = LocalAccountServiceConfiguration.current
        ListRow(configuration.userIdConfiguration.idType.stringResource) {
            Text(value)
        }
    }

    @Composable
    override fun EntryComposable(state: MutableState<String>) {
        val configuration = LocalAccountServiceConfiguration.current

        VerifiableTextField(configuration.userIdConfiguration.idType.stringResource, state)
        // TODO: TextContentTupe, KeyboardType, Disable field assistants
    }

    override fun compute(repository: SharedRepository<AccountAnchor>): String {
        return repository[this as AccountKey<String>]
            ?: repository[AccountKeys.accountId]
            ?: TODO("This last access should actually not be nullable...")
    }
}

val AccountKeys.userId: ComputedAccountKey<String>
    get() = AccountUserIdKey

var AccountDetails.userId: String
    get() = this.storage[AccountKeys.userId]
    set(value) { this.storage[AccountKeys.userId] = value }