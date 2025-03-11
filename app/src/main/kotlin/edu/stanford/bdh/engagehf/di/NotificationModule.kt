package edu.stanford.bdh.engagehf.di

import android.content.ComponentName
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.stanford.bdh.engagehf.MainActivity
import edu.stanford.spezi.modules.notification.notifier.Notifications
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {

    @Notifications.TargetActivity
    @Provides
    @Singleton
    fun provideMainActivityComponentName(
        @ApplicationContext context: Context,
    ): ComponentName = ComponentName(context, MainActivity::class.java)
}
