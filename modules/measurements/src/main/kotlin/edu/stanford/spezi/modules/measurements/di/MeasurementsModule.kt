package edu.stanford.spezi.modules.measurements.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.modules.measurements.MeasurementsRepository
import edu.stanford.spezi.modules.measurements.MeasurementsRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class MeasurementsModule {

    @Binds
    internal abstract fun bindMeasurementsRepository(
        impl: MeasurementsRepositoryImpl,
    ): MeasurementsRepository
}
