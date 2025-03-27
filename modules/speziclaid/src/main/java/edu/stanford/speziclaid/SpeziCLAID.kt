package edu.stanford.speziclaid

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.speziclaid.datastore.DataStorer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpeziCLAID {

    @Provides
    @Singleton
    fun provideCLAIDRuntime(application: Application): CLAIDRuntime {
        return CLAIDRuntime(application)
    }

    @Provides
    @Singleton
    fun provideDataStorer(application: Application): DataStorer {
        return DataStorer(application)
    }
}