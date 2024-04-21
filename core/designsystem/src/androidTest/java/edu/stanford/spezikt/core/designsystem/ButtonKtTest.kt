package edu.stanford.spezikt.core.designsystem

import androidx.compose.material3.Text
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import edu.stanford.spezikt.core.designsystem.component.SpeziButton
import edu.stanford.spezikt.core.designsystem.theme.SpeziKtTheme
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class ButtonKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun speziButtonIsDisplayed() {
        val text = "Test Button"
        composeTestRule.setContent {
            SpeziKtTheme {
                SpeziButton(
                    onClick = { },
                    content = { Text(text = text) }
                )
            }
        }

        composeTestRule.onNodeWithText(text).assertIsDisplayed()
    }

    @Test
    fun speziButton_isEnabled() {
        val text = "Test Button"
        composeTestRule.setContent {
            SpeziKtTheme {
                SpeziButton(
                    onClick = { /**/ },
                    content = { Text(text = text) }
                )
            }
        }

        composeTestRule.onNodeWithText(text).assertHasClickAction()
    }

    @Test
    fun speziButton_isDisabled() {
        val text = "Test Button"
        composeTestRule.setContent {
            SpeziKtTheme {
                SpeziButton(
                    onClick = { },
                    enabled = false,
                    content = { Text(text = text) }
                )
            }
        }

        composeTestRule.onNodeWithText(text).assert(isNotEnabled())
    }

    @Test
    fun speziButton_onClick() {
        val text = "Test Button"
        var clicked = false
        composeTestRule.setContent {
            SpeziKtTheme {
                SpeziButton(
                    onClick = { clicked = true },
                    content = { Text(text = text) }
                )
            }
        }

        runBlocking {
            composeTestRule.onNodeWithText(text).performClick()
        }

        assert(clicked)
    }
}