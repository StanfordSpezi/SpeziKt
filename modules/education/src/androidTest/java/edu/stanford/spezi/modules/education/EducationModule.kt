package edu.stanford.spezi.modules.education

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.stanford.spezi.modules.education.videos.data.repository.EducationRepository
import io.mockk.mockk

@Module
@InstallIn(SingletonComponent::class)
class EducationModule {
    private val educationRepository: EducationRepository = mockk(relaxed = true)

    @Provides
    fun provideEducationRepository(): EducationRepository {
        return educationRepository
    }
}
