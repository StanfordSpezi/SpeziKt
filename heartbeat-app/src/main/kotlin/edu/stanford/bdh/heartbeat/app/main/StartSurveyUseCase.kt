package edu.stanford.bdh.heartbeat.app.main

import edu.stanford.bdh.heartbeat.app.account.AccountManager
import edu.stanford.bdh.heartbeat.app.choir.ChoirRepository
import edu.stanford.bdh.heartbeat.app.choir.api.types.AssessmentStep
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.storage.credential.KeyValueStorage
import edu.stanford.spezi.storage.credential.Storage
import javax.inject.Inject

class StartSurveyUseCase @Inject constructor(
    private val choirRepository: ChoirRepository,
    private val accountManager: AccountManager,
    @Storage.Encrypted
    private val storage: KeyValueStorage,
) {

    private val logger by speziLogger()

    suspend operator fun invoke(): Result<AssessmentStep> {
        val userId = accountManager.getAccountInfo()?.id.orEmpty()
        val storageKey = getStorageKey(userId = userId)
        val pendingToken = storage.getString(key = storageKey)

        return if (!pendingToken.isNullOrBlank()) {
            logger.i { "Pending token for $userId available. Starting assessment: $pendingToken" }
            choirRepository.startAssessment(token = pendingToken)
        } else {
            logger.i { "No token found, getting onboarding" }
            choirRepository.getOnboarding()
        }.onSuccess { assessmentStep ->
            assessmentStep.displayStatus.surveyToken?.let { token ->
                logger.i { "storing assessment token $token for user $userId" }
                storage.putString(key = storageKey, value = token)
            }
        }
    }

    private fun getStorageKey(userId: String) = "pending_session:$userId"
}
