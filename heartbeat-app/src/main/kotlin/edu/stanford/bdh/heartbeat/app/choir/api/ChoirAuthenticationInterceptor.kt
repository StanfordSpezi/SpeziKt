package edu.stanford.bdh.heartbeat.app.choir.api

import edu.stanford.bdh.heartbeat.app.account.AccountManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ChoirAuthenticationInterceptor @Inject constructor(
    private val accountManager: AccountManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token: String? = runBlocking {
            accountManager.getToken().getOrNull()
        }
        val request = chain.request().newBuilder()
        token?.let {
            request.addHeader("Authorization", "Bearer $it")
        }
        return chain.proceed(request.build())
    }
}
