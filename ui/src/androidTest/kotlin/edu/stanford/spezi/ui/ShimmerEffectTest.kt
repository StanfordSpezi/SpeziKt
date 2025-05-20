package edu.stanford.spezi.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.testing.ui.onNodeWithIdentifier
import org.junit.Rule
import org.junit.Test

class ShimmerEffectTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val identifier = Identifier.ROOT

    @Test
    fun testShimmerBox() {
        composeTestRule.setContent {
            ShimmerEffectBox(
                modifier = Modifier.fillMaxSize().testIdentifier(identifier)
            )
        }

        composeTestRule.onNodeWithIdentifier(identifier).assertIsDisplayed()
    }

    @Test
    fun testCircleShimmerBox() {
        composeTestRule.setContent {
            CircleShimmerEffect(
                modifier = Modifier.fillMaxSize().testIdentifier(identifier)
            )
        }

        composeTestRule.onNodeWithIdentifier(identifier).assertIsDisplayed()
    }

    @Test
    fun testRectangleShimmerBox() {
        composeTestRule.setContent {
            RectangleShimmerEffect(
                modifier = Modifier.fillMaxSize().testIdentifier(identifier)
            )
        }

        composeTestRule.onNodeWithIdentifier(identifier).assertIsDisplayed()
    }

    private enum class Identifier {
        ROOT,
    }
}
