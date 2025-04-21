package edu.stanford.bdh.engagehf.phonenumber

import android.content.Context
import com.google.firebase.functions.FirebaseFunctions
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.core.coroutines.Dispatching
import edu.stanford.spezi.core.logging.speziLogger
import edu.stanford.spezi.modules.account.manager.UserSessionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Locale
import javax.inject.Inject

class PhoneNumberService @Inject constructor(
    private val firebaseFunctions: FirebaseFunctions,
    private val userSessionManager: UserSessionManager,
    @ApplicationContext private val context: Context,
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) {
    private val phoneNumberUtil by lazy { PhoneNumberUtil.getInstance() }

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

    suspend fun deletePhoneNumber(phoneNumber: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val uid = userSessionManager.getUserUid().orEmpty()
            val data = mapOf(
                "phoneNumber" to phoneNumber,
                "userId" to uid
            )
            firebaseFunctions.getHttpsCallable("deletePhoneNumber").call(data).await().let { }
        }.onFailure {
            logger.e(it) { "Error starting phone number verification" }
        }
    }

    suspend fun getAllCountryCodes(): List<CountryCode> = withContext(ioDispatcher) {
        val countryFlagsMap = context.resources.openRawResource(R.raw.country_emojis)
            .bufferedReader()
            .use { it.readText() }
            .let { Json.decodeFromString<CountryFlags>(it) }
        val countryCodes = phoneNumberUtil.supportedRegions.mapNotNull { iso ->
            runCatching {
                CountryCode(
                    name = Locale("", iso).getDisplayCountry(Locale.getDefault()),
                    iso = iso,
                    dialCode = "+${phoneNumberUtil.getCountryCodeForRegion(iso)}",
                    emoji = countryFlagsMap.emojis[iso] ?: "🌐",
                )
            }.getOrNull()
        }
        countryCodes.sortedBy { it.name }
    }

    fun isPhoneNumberValid(phoneNumber: String, isoCode: String): Boolean {
        return runCatching {
            val number = phoneNumberUtil.parse(phoneNumber, isoCode.uppercase())
            phoneNumberUtil.isValidNumber(number)
        }.getOrDefault(false)
    }

    fun format(phoneNumber: String): String = runCatching {
        val parsedNumber = phoneNumberUtil.parse(phoneNumber, null)
        phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
    }.getOrDefault(phoneNumber)

    @Serializable
    data class CountryFlags(
        val emojis: Map<String, String>,
    )
}
