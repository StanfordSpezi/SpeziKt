package edu.stanford.bdh.engagehf.messages

import com.google.firebase.functions.FirebaseFunctions
import edu.stanford.spezi.core.utils.JsonMap
import edu.stanford.spezi.module.account.manager.UserSessionManager
import edu.stanford.spezi.spezi.core.logging.coroutines.di.Dispatching
import edu.stanford.spezi.spezi.core.logging.speziLogger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HealthSummaryRepository @Inject constructor(
    private val userSessionManager: UserSessionManager,
    private val firebaseFunctions: FirebaseFunctions,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    private val logger by speziLogger()

    suspend fun getHealthSummary(): Result<ByteArray> = withContext(ioDispatcher) {
        runCatching {
            val uid = userSessionManager.getUserUid()
                ?: error("User not authenticated")
            val result = firebaseFunctions.getHttpsCallable("exportHealthSummary")
                .call(mapOf("userId" to uid))
                .await()
            val pdfBase64 = (result.data as? JsonMap)?.get("content") as? String
                ?: error("Invalid function response")
            val pdfBytes = android.util.Base64.decode(pdfBase64, android.util.Base64.DEFAULT)
            pdfBytes
        }.onFailure {
            logger.e(it) { "Error while fetching health summary" }
        }
    }
}
