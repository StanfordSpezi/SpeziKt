package edu.stanford.spezi.core.design

import androidx.compose.material3.Text
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import edu.stanford.spezi.spezi.ui.helpers.Button
import edu.stanford.spezi.spezi.ui.helpers.theme.SpeziTheme
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class ButtonKtTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buttonIsDisplayed() {
        val text = "Test Button"
        composeTestRule.setContent {
            SpeziTheme {
                Button(
                    onClick = { },
                    content = { Text(text = text) }
                )
            }
        }

        composeTestRule.onNodeWithText(text).assertIsDisplayed()
    }

    @Test
    fun button_isEnabled() {
        val text = "Test Button"
        composeTestRule.setContent {
            SpeziTheme {
                Button(
                    onClick = { /**/ },
                    content = { Text(text = text) }
                )
            }
        }

        composeTestRule.onNodeWithText(text).assertHasClickAction()
    }

    @Test
    fun button_isDisabled() {
        val text = "Test Button"
        composeTestRule.setContent {
            SpeziTheme {
                Button(
                    onClick = { },
                    enabled = false,
                    content = { Text(text = text) }
                )
            }
        }

        composeTestRule.onNodeWithText(text).assert(isNotEnabled())
    }

    @Test
    fun button_onClick() {
        val text = "Test Button"
        var clicked = false
        composeTestRule.setContent {
            SpeziTheme {
                Button(
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
