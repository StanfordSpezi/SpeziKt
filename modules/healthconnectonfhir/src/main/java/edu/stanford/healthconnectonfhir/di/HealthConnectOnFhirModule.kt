package edu.stanford.healthconnectonfhir.di

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.healthconnectonfhir.ObservationsDocumentMapper
import edu.stanford.healthconnectonfhir.ObservationsDocumentMapperImpl
import edu.stanford.healthconnectonfhir.RecordToObservationMapper
import edu.stanford.healthconnectonfhir.RecordToObservationMapperImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HealthConnectOnFhirModule {

    @Provides
    fun provideRecordToObservationMapper(
        impl: RecordToObservationMapperImpl,
    ): RecordToObservationMapper = impl

    @Provides
    fun provideObservationsDocumentMapper(
        impl: ObservationsDocumentMapperImpl,
    ): ObservationsDocumentMapper = impl

    @Provides
    @Singleton
    internal fun provideGson() = Gson()

    @Provides
    @Singleton
    internal fun provideIParser(): IParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
}
