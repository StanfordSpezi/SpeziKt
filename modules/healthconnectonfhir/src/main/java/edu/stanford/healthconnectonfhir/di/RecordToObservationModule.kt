package edu.stanford.healthconnectonfhir.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.healthconnectonfhir.RecordToObservationMapper
import edu.stanford.healthconnectonfhir.RecordToObservationMapperImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RecordToObservationModule {

    @Binds
    abstract fun bindRecordToObservationMapper(
        impl: RecordToObservationMapperImpl,
    ): RecordToObservationMapper
}
