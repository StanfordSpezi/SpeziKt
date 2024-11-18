package edu.stanford.spezi.module.account.account.compositionLocal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import edu.stanford.spezi.module.account.account.value.AccountKey
import java.util.Date

data class SignupProviderCompliance internal constructor(
    private val creationDate: Date = Date(),
    val visualizedAccountKeys: VisualizedAccountKeys,
) {
    sealed interface VisualizedAccountKeys {
        data object All : VisualizedAccountKeys
        data class Only(val keys: List<AccountKey<*>>) : VisualizedAccountKeys
    }

    companion object {
        val compliant: SignupProviderCompliance
            get() = SignupProviderCompliance(visualizedAccountKeys = VisualizedAccountKeys.All)

        fun askedFor(keys: List<AccountKey<*>>) =
            SignupProviderCompliance(visualizedAccountKeys = VisualizedAccountKeys.Only(keys))
    }
}

private data class SignupProviderComplianceReader(
    var entry: Entry? = null,
) {
    data class Entry(
        val compliance: SignupProviderCompliance,
        val date: Date = Date(),
    )
}

private val LocalSignupProviderComplianceReaders = compositionLocalOf { emptyList<SignupProviderComplianceReader>() }

@Composable
fun ReportSignupProviderCompliance(compliance: SignupProviderCompliance?) {
    compliance?.let {
        val newEntry = SignupProviderComplianceReader.Entry(it)
        LocalSignupProviderComplianceReaders.current.forEach { reader ->
            val oldEntry = reader.entry
            if (oldEntry == null || oldEntry.date > newEntry.date) {
                reader.entry = newEntry
            }
        }
    }
}

@Composable
internal fun ReceiveSignupProviderCompliance(
    action: (SignupProviderCompliance?) -> Unit,
    content: @Composable () -> Unit
) {
    val newReader = remember { SignupProviderComplianceReader() }
    val existingReaders = LocalSignupProviderComplianceReaders.current
    CompositionLocalProvider(
        LocalSignupProviderComplianceReaders provides (existingReaders + newReader)
    ) {
        content()
    }
    LaunchedEffect(newReader.entry) {
        action(newReader.entry?.compliance)
    }
}
