package edu.stanford.spezi.modules.notification.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.modules.notification.NotificationPermissions
import edu.stanford.spezi.modules.notification.NotificationPermissionsImpl
import edu.stanford.spezi.modules.notification.fcm.DeviceRegistrationService
import edu.stanford.spezi.modules.notification.fcm.DeviceRegistrationServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {

    /**
     * This dependency is provided in a separate module to be replaced for instrumentation tests
     * that run for android versions 31 and 34 where we can't use grant rule
     */
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class NotificationPermissionsBinding {
        @Binds
        internal abstract fun bindNotificationPermissions(
            impl: NotificationPermissionsImpl,
        ): NotificationPermissions
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {
        @Binds
        internal abstract fun bindDeviceRegistrationService(
            impl: DeviceRegistrationServiceImpl,
        ): DeviceRegistrationService
    }

    @Provides
    @Singleton
    internal fun provideFirebaseMessaging(): FirebaseMessaging =
        FirebaseMessaging.getInstance()

    @Provides
    fun provideNotificationManagerCompat(@ApplicationContext context: Context): NotificationManagerCompat =
        NotificationManagerCompat.from(context)
}
