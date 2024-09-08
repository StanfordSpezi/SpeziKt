package edu.stanford.bdh.engagehf.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.bdh.engagehf.bluetooth.service.mapper.MeasurementMapper
import edu.stanford.bdh.engagehf.bluetooth.service.mapper.MeasurementMapperImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class BluetoothModule {
    /**
     * Binds the implementation of [MeasurementMapper] interface.
     *
     * @param impl The implementation of [MeasurementMapper].
     * @return An instance of [MeasurementMapper].
     */
    @Binds
    internal abstract fun bindMeasurementMapper(impl: MeasurementMapperImpl): MeasurementMapper
}
