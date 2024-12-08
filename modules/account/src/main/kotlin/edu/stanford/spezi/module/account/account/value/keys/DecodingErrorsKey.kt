package edu.stanford.spezi.module.account.account.value.keys

import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.collections.AccountAnchor
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails

private object DecodingErrorsKey : KnowledgeSource<AccountAnchor, List<Pair<AccountKey<*>, Throwable>>>

var AccountDetails.decodingErrors: List<Pair<AccountKey<*>, Throwable>>?
    get() = this.storage[DecodingErrorsKey]
    set(value) { this.storage[DecodingErrorsKey] = value }
