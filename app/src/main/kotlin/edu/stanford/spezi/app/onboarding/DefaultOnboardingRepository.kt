package edu.stanford.spezi.app.onboarding

import edu.stanford.spezi.core.coroutines.di.Dispatching
import edu.stanford.spezi.core.design.R
import edu.stanford.spezi.module.onboarding.onboarding.Area
import edu.stanford.spezi.module.onboarding.onboarding.OnboardingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultOnboardingRepository @Inject constructor(
    @Dispatching.IO private val ioDispatcher: CoroutineDispatcher,
) : OnboardingRepository {

    override suspend fun getAreas(): Result<List<Area>> = Result.success(
        listOf(
            Area(
                title = "Join the Study",
                iconId = R.drawable.ic_groups,
                description = "Connect to your study via an invitation code from the researchers."
            ),
            Area(
                title = "Complete Health Checks",
                iconId = R.drawable.ic_assignment,
                description = "Record and report health data automatically according to a schedule set by the research team."
            ),
            Area(
                title = "Visualize Data",
                iconId = R.drawable.ic_vital_signs,
                description = "Visualize your heart health progress throughout participation in the study."
            )
        )
    )


    override suspend fun getTitle(): Result<String> = withContext(ioDispatcher) {
        runCatching {
            "Welcome to ENGAGE-HF"
        }
    }

    override suspend fun getSubtitle(): Result<String> = withContext(ioDispatcher) {
        runCatching {
            "Remote study participation made easy."
        }
    }
}