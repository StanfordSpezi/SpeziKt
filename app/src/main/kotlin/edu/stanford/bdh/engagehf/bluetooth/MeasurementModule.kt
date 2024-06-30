package edu.stanford.bdh.engagehf.bluetooth

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.DefaultMeasurementToRecordMapper
import edu.stanford.bdh.engagehf.bluetooth.data.mapper.MeasurementToRecordMapper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MeasurementModule {

    @Singleton
    @Provides
    fun provideMeasurementToRecordMapper(): MeasurementToRecordMapper =
        DefaultMeasurementToRecordMapper()
}
