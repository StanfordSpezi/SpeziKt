package edu.stanford.spezi.modules.healthconnectonfhir.di

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.modules.healthconnectonfhir.ObservationsDocumentMapper
import edu.stanford.spezi.modules.healthconnectonfhir.QuestionnaireDocumentMapper
import edu.stanford.spezi.modules.healthconnectonfhir.RecordToObservationMapper
import edu.stanford.spezi.modules.healthconnectonfhir.internal.ObservationsDocumentMapperImpl
import edu.stanford.spezi.modules.healthconnectonfhir.internal.QuestionnaireDocumentMapperImpl
import edu.stanford.spezi.modules.healthconnectonfhir.internal.RecordToObservationMapperImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HealthConnectOnFhirModule {

    @Provides
    internal fun provideRecordToObservationMapper(
        impl: RecordToObservationMapperImpl,
    ): RecordToObservationMapper = impl

    @Provides
    internal fun provideObservationsDocumentMapper(
        impl: ObservationsDocumentMapperImpl,
    ): ObservationsDocumentMapper = impl

    @Provides
    internal fun provideQuestionnaireDocumentMapper(
        impl: QuestionnaireDocumentMapperImpl,
    ): QuestionnaireDocumentMapper = impl

    @Provides
    @Singleton
    internal fun provideGson() = Gson()

    @Provides
    @Singleton
    internal fun provideIParser(): IParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
}
