package edu.stanford.spezi.core.notification.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class NotificationModule {
    @Provides
    @Singleton
    internal fun provideFirebaseMessaging(): FirebaseMessaging =
        FirebaseMessaging.getInstance()

    @Provides
    fun provideNotificationManagerCompat(@ApplicationContext context: Context): NotificationManagerCompat =
        NotificationManagerCompat.from(context)
}
