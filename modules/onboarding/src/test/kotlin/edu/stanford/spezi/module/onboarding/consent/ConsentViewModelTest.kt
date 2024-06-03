package edu.stanford.spezi.module.onboarding.consent

import androidx.compose.ui.graphics.Path
import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.testing.CoroutineTestRule
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConsentViewModelTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val repository: ConsentRepository = mockk(relaxed = true)
    private lateinit var viewModel: ConsentViewModel

    @Before
    fun setup() {
        viewModel = ConsentViewModel(repository)
    }

    @Test
    fun `it should update firstName on TextFieldUpdate action correctly`() = runTestUnconfined {
        // Given
        val name = "Kilian"
        val action = ConsentAction.TextFieldUpdate(name, TextFieldType.FIRST_NAME)

        // When
        viewModel.onAction(action)

        // Then
        val uiState = viewModel.uiState.first()
        assertThat(name).isEqualTo(uiState.firstName.value)
    }

    @Test
    fun `it should handle AddPath action correctly`() = runTestUnconfined {
        // Given
        val action = ConsentAction.AddPath(Path())

        // When
        viewModel.onAction(action)

        // Then
        val uiState = viewModel.uiState.first()
        assertThat(uiState.paths.size).isEqualTo(1)
    }

    @Test
    fun `it should handle Undo action correctly`() = runTestUnconfined {
        // Given
        viewModel.onAction(ConsentAction.AddPath(Path()))
        val action = ConsentAction.Undo

        // When
        viewModel.onAction(action)

        // Then
        val uiState = viewModel.uiState.first()
        assertThat(uiState.paths.size).isEqualTo(0)
    }

    @Test
    fun `init block should fetch ConsentData correctly`() = runTestUnconfined {
        // Given
        val consentData = ConsentData("markdownText") {}
        coEvery { repository.getConsentData() } returns consentData

        // When
        viewModel = ConsentViewModel(repository)

        // Then
        val uiState = viewModel.uiState.first()
        assertThat(consentData.markdownText).isEqualTo(uiState.markdownText)
    }
}