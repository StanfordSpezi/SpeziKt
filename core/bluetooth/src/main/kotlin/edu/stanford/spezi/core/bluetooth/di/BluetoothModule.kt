package edu.stanford.spezi.core.bluetooth.di

import android.bluetooth.BluetoothAdapter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.core.bluetooth.api.BLEService
import edu.stanford.spezi.core.bluetooth.domain.BLEServiceImpl

/**
 * Dagger Hilt module for providing Bluetooth-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
class BluetoothModule {

    /**
     * Provides the Bluetooth adapter.
     *
     * @return The Bluetooth adapter instance obtained from the system service.
     */
    @Suppress("DEPRECATION")
    @Provides
    fun provideBluetoothAdapter(): BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    /**
     * Dagger Hilt module for providing bindings.
     */
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class Bindings {

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
