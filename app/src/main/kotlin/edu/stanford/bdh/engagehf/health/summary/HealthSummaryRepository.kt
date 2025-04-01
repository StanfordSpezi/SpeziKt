package edu.stanford.bdh.engagehf.health.summary

import com.google.firebase.functions.FirebaseFunctions
import edu.stanford.spezi.core.coroutines.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import edu.stanford.spezi.modules.utils.JsonMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.Instant
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

    suspend fun getShareHealthSummaryData(): Result<ShareHealthSummaryData> = withContext(ioDispatcher) {
        runCatching {
            val uid = userSessionManager.getUserUid() ?: error("User not authenticated")
            val result = firebaseFunctions.getHttpsCallable("shareHealthSummary")
                .call(mapOf("userId" to uid))
                .await()

            val data = result.data as? JsonMap ?: error("Invalid function response")

            val url = data["url"] as? String
            val code = data["code"] as? String
            val expiresAtString = data["expiresAt"] as? String
            val expiresAt = Instant.parse(expiresAtString)

            if (url != null && code != null && expiresAt != null) {
                ShareHealthSummaryData(
                    url = url,
                    code = code,
                    expiresAt = expiresAt
                )
            } else {
                error("Invalid function response")
            }
        }
    }
}
