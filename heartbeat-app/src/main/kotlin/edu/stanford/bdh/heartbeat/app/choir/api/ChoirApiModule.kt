package edu.stanford.bdh.heartbeat.app.choir.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChoirApiModule {
    private const val USE_PRODUCTION = false
    private const val BASE_URL_DEMO = "https://choir-demo.med.stanford.edu/afib-api/v1/"
    private const val BASE_URL_PRODUCTION = "https://choir.med.stanford.edu/afib-api/v1/"

    @Provides
    @Singleton
    fun providesChoirApi(authInterceptor: ChoirAuthenticationInterceptor): ChoirApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(if (USE_PRODUCTION) BASE_URL_PRODUCTION else BASE_URL_DEMO)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()
            .create(ChoirApi::class.java)
    }
}
