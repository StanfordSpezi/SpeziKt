package edu.stanford.spezi.core.notification.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.notification.NotificationPermissions
import edu.stanford.spezi.core.notification.NotificationPermissionsImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {

    @Module
    @InstallIn(SingletonComponent::class)
    internal abstract class Bindings {
        @Binds
        internal abstract fun bindNotificationPermissions(
            impl: NotificationPermissionsImpl,
        ): NotificationPermissions
    }

    @Provides
    @Singleton
    internal fun provideFirebaseMessaging(): FirebaseMessaging =
        FirebaseMessaging.getInstance()

    @Provides
    fun provideNotificationManagerCompat(@ApplicationContext context: Context): NotificationManagerCompat =
        NotificationManagerCompat.from(context)
}
