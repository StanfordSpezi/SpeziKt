package edu.stanford.bdh.engagehf.messages

import com.google.firebase.functions.FirebaseFunctions
import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.module.account.manager.UserSessionManager
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

    suspend fun findHealthSummaryByUserId(): Result<ByteArray> = withContext(ioDispatcher) {
        runCatching {
            val uid = userSessionManager.getUserUid()
                ?: throw IllegalStateException("User not authenticated")
            val result = firebaseFunctions.getHttpsCallable("exportHealthSummary")
                .call(mapOf("userId" to uid))
                .await()
            val resultData = result.data as? Map<*, *>
                ?: throw IllegalStateException("Invalid function response")
            val pdfBase64 = resultData["content"] as? String
                ?: throw IllegalStateException("No content found in function response")
            val pdfBytes = android.util.Base64.decode(pdfBase64, android.util.Base64.DEFAULT)
            pdfBytes
        }.onFailure {
            logger.e(it) { "Error while fetching health summary" }
        }
    }
}
