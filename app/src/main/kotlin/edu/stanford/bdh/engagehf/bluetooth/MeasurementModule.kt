package edu.stanford.bdh.engagehf.bluetooth

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.DefaultMeasurementToObservationMapper
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.DefaultMeasurementToRecordMapper
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MeasurementToObservationMapper
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MeasurementToRecordMapper

@Module
@InstallIn(SingletonComponent::class)
abstract class MeasurementModule {

    @Binds
    internal abstract fun bindMeasurementToRecordMapper(
        defaultMeasurementToRecordMapper: DefaultMeasurementToRecordMapper,
    ): MeasurementToRecordMapper

    @Binds
    internal abstract fun bindMeasurementToObservationMapper(
        defaultMeasurementToObservationMapper: DefaultMeasurementToObservationMapper,
    ): MeasurementToObservationMapper
}
