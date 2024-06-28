package edu.stanford.bdh.engagehf.education

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.modules.education.EducationRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class EducationModule {
    @Binds
    abstract fun bindEducationRepository(
        engageEducationRepository: EngageEducationRepository,
    ): EducationRepository
}
