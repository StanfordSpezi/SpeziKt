package edu.stanford.spezi.sample.app

import android.app.Application
import androidx.health.connect.client.records.Record
import edu.stanford.spezi.account.AccountKeys
import edu.stanford.spezi.account.InMemoryAccountService
import edu.stanford.spezi.account.InMemoryAccountStorageProvider
import edu.stanford.spezi.account.accountConfiguration
import edu.stanford.spezi.core.Configuration
import edu.stanford.spezi.core.SpeziApplication
import edu.stanford.spezi.core.logging.SpeziLogger
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.core.viewmodel.viewModel
import edu.stanford.spezi.health.CollectionMode
import edu.stanford.spezi.health.HealthConstraint
import edu.stanford.spezi.health.RecordType
import edu.stanford.spezi.health.health
import edu.stanford.spezi.sample.app.health.HealthPrivacyScreen
import edu.stanford.spezi.sample.app.health.HealthViewModel
import edu.stanford.spezi.sample.app.home.HomeViewModel
import kotlin.time.Duration.Companion.seconds

class SampleApplication : Application(), SpeziApplication, HealthConstraint {
    private val logger by speziLogger()

    override val configuration: Configuration = Configuration(standard = this) {
        singleton { Navigator() }

        viewModel {
            HomeViewModel(
                navigator = dependency(),
                account = dependency(),
            )
        }
        viewModel {
            HealthViewModel(
                navigator = dependency(),
                health = dependency(),
            )
        }

        health {
            requestReadAccess(RecordType.bloodPressure, RecordType.weight)
            requestWriteAccess(RecordType.heartRate, RecordType.steps)
            collectRecord(
                recordType = RecordType.steps,
                start = CollectionMode.Automatic(pollingInterval = 5.seconds),
                continueInBackground = true,
            )
            privacy {
                composable { HealthPrivacyScreen() }
            }
        }

        accountConfiguration(
            service = InMemoryAccountService(),
            storageProvider = InMemoryAccountStorageProvider(),
            configuration = {
                requires(key = AccountKeys.accountId)
                collects(key = AccountKeys.name)
                collects(key = AccountKeys.email)
                collects(key = AccountKeys.password)
                collects(key = AccountKeys.dateOfBirth)
                supports(key = AccountKeys.genderIdentity)
                manual(key = AccountKeys.userId)
            }
        )
    }

    override fun onCreate() {
        super.onCreate()

        SpeziLogger.setLoggingEnabled(enabled = true)
    }

    override suspend fun <T : Record> handleDeletedRecords(deletedRecordIds: Set<String>, type: RecordType<out T>) {
        logger.i { "Received deleted records callback: $deletedRecordIds" }
    }

    override suspend fun <T : Record> handleNewRecords(addedRecords: Set<T>, type: RecordType<out T>) {
        logger.i { "Received added records callback of type: ${type.type}" }
    }

    override suspend fun <T : Record> onFullyResyncRequired(type: RecordType<out T>) {
        logger.i { "Received on fully resync required callback of type: ${type.type}" }
    }
}
