package edu.stanford.spezi.core.bluetooth.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.bluetooth.api.BLEService
import edu.stanford.spezi.core.bluetooth.data.mapper.MeasurementMapper
import edu.stanford.spezi.core.bluetooth.data.mapper.MeasurementMapperImpl
import edu.stanford.spezi.core.bluetooth.data.model.BLEServiceType
import edu.stanford.spezi.core.bluetooth.data.model.SupportedServices
import edu.stanford.spezi.core.bluetooth.domain.BLEServiceImpl
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing Bluetooth-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
class BluetoothModule {

    /**
     * Provides the supported BLE services.
     *
     * @return An instance of [SupportedServices] containing the supported BLE service types.
     */
    @Singleton
    @Provides
    internal fun provideSupportedServices(): SupportedServices = SupportedServices(services = BLEServiceType.entries)

    /**
     * Provides the Bluetooth adapter.
     *
     * @param context The application context.
     * @return The Bluetooth adapter instance obtained from the system service.
     */
    @Provides
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter =
        context.getSystemService(BluetoothManager::class.java).adapter

    /**
     * Dagger Hilt module for providing bindings.
     */
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {

        /**
         * Binds the implementation of [MeasurementMapper] interface.
         *
         * @param impl The implementation of [MeasurementMapper].
         * @return An instance of [MeasurementMapper].
         */
        @Binds
        internal abstract fun bindMeasurementMapper(impl: MeasurementMapperImpl): MeasurementMapper

        /**
         * Binds the implementation of [BLEService] interface.
         *
         * @param impl The implementation of [BLEService].
         * @return An instance of [BLEService].
         */
        @Binds
        internal abstract fun bindSpeziBLEService(impl: BLEServiceImpl): BLEService
    }
}
