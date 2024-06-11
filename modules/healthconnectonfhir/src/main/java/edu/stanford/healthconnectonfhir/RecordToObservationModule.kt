package edu.stanford.healthconnectonfhir

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RecordToObservationModule {

    @Binds
    abstract fun bindRecordToObservationMapper(
        impl: RecordToObservationMapperImpl,
    ): RecordToObservationMapper
}
