package edu.stanford.spezi.module.account.account.views.overview

import edu.stanford.spezi.module.account.account.value.AccountKey
import edu.stanford.spezi.module.account.account.value.AccountKeyCategory
import edu.stanford.spezi.module.account.account.value.collections.AccountDetails

data class AccountOverviewValuesComparator(
    val accountDetails: AccountDetails,
    val addedDetails: Map<AccountKeyCategory, List<AccountKey<*>>>,
    val removedDetails: Map<AccountKeyCategory, List<AccountKey<*>>>,
) : Comparator<AccountKey<*>> {
    override fun compare(lhs: AccountKey<*>, rhs: AccountKey<*>): Int {
        val lhsContained = accountDetails.contains(lhs) && !(removedDetails[lhs.category]?.contains(lhs) ?: false)
        val rhsContained = accountDetails.contains(rhs) && !(removedDetails[rhs.category]?.contains(rhs) ?: false)

        if (lhsContained || rhsContained) {
            return if (lhsContained == rhsContained) {
                0
            } else if (!rhsContained) {
                -1
            } else {
                1
            }
        }

        val lhsIndex = addedDetails[lhs.category]?.indexOf(lhs)
        val rhsIndex = addedDetails[rhs.category]?.indexOf(rhs)

        return compareIndices(lhsIndex, rhsIndex)
    }

    private fun compareIndices(lhs: Int?, rhs: Int?): Int {
        return lhs?.let {
            rhs?.let { lhs.compareTo(rhs) } ?: 0
        } ?: rhs?.let { -1 } ?: 0
    }
}
