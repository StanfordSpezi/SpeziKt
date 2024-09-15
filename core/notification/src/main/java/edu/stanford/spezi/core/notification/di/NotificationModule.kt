package edu.stanford.spezi.core.notification.di

import com.google.firebase.messaging.FirebaseMessaging
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.notification.notifier.NotificationNotifier
import edu.stanford.spezi.core.notification.notifier.SystemTrayNotificationNotifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class NotificationModule {
    @Provides
    @Singleton
    internal fun provideFirebaseMessaging(): FirebaseMessaging =
        FirebaseMessaging.getInstance()

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {
        @Binds
        abstract fun bindNotificationNotifier(
            systemTrayNotificationNotifier: SystemTrayNotificationNotifier,
        ): NotificationNotifier
    }
}
