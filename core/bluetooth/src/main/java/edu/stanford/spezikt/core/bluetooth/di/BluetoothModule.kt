package edu.stanford.spezikt.core.bluetooth.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezikt.core.bluetooth.api.BLEService
import edu.stanford.spezikt.core.bluetooth.data.mapper.MeasurementMapper
import edu.stanford.spezikt.core.bluetooth.data.mapper.MeasurementMapperImpl
import edu.stanford.spezikt.core.bluetooth.data.model.BLEServiceType
import edu.stanford.spezikt.core.bluetooth.data.model.SupportedServices
import edu.stanford.spezikt.core.bluetooth.domain.BLEServiceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class BluetoothModule {

    @Singleton
    @Provides
    internal fun provideSupportedServices(): SupportedServices = SupportedServices(services = BLEServiceType.entries)

    @Provides
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter =
        context.getSystemService(BluetoothManager::class.java).adapter

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {

        @Binds
        internal abstract fun bindMeasurementMapper(impl: MeasurementMapperImpl): MeasurementMapper

        @Binds
        internal abstract fun bindSpeziBLEService(impl: BLEServiceImpl): BLEService
    }
}