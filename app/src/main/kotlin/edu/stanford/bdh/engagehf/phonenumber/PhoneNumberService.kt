package edu.stanford.bdh.engagehf.phonenumber

import com.google.firebase.functions.FirebaseFunctions
import edu.stanford.spezi.core.coroutines.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PhoneNumberService @Inject constructor(
    private val firebaseFunctions: FirebaseFunctions,
    private val userSessionManager: UserSessionManager,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    private val logger by speziLogger()

    suspend fun startPhoneNumberVerification(phoneNumber: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val uid = userSessionManager.getUserUid().orEmpty()
            val data = mapOf(
                "phoneNumber" to phoneNumber,
                "userId" to uid
            )
            firebaseFunctions.getHttpsCallable("startPhoneNumberVerification").call(data).await().let { }
        }.onFailure {
            logger.e(it) { "Error starting phone number verification" }
        }
    }

    suspend fun checkPhoneNumberVerification(
        code: String,
        phoneNumber: String,
    ): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val uid = userSessionManager.getUserUid().orEmpty()
            val data = mapOf(
                "phoneNumber" to phoneNumber,
                "code" to code,
                "userId" to uid
            )
            firebaseFunctions.getHttpsCallable("checkPhoneNumberVerification").call(data).await().let { }
        }.onFailure {
            logger.e(it) { "Error checking phone number verification code" }
        }
    }
}
