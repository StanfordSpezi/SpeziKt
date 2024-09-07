package edu.stanford.spezi.core.notification.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.notification.notifier.NotificationNotifier
import edu.stanford.spezi.core.notification.notifier.SystemTrayNotificationNotifier

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationModule {
    @Binds
    abstract fun bindNotificationNotifier(
        systemTrayNotificationNotifier: SystemTrayNotificationNotifier,
    ): NotificationNotifier
}
