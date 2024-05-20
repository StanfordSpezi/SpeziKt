package edu.stanford.spezikt.onboarding

import edu.stanford.spezikt.core.design.R
import edu.stanford.spezikt.coroutines.di.Dispatching
import edu.stanford.spezikt.spezi_module.onboarding.onboarding.Area
import edu.stanford.spezikt.spezi_module.onboarding.onboarding.OnboardingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultOnboardingRepository @Inject constructor(
    @Dispatching.IO private val scope: CoroutineScope,
) : OnboardingRepository {

    override suspend fun getAreas(): Result<List<Area>> = withContext(scope.coroutineContext) {
        try {
            Result.success(
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
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTitle(): Result<String> = withContext(scope.coroutineContext) {
        try {
            Result.success("Welcome to ENGAGE-HF")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSubtitle(): Result<String> = withContext(scope.coroutineContext) {
        try {
            Result.success("Remote study participation made easy.")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}