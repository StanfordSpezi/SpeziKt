package edu.stanford.spezi.core.design.component

import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import edu.stanford.spezi.core.testing.onAllNodes
import edu.stanford.spezi.core.testing.onNodeWithIdentifier
import edu.stanford.spezi.core.utils.extensions.testIdentifier
import org.junit.Rule
import org.junit.Test

class RepeatingLazyColumnTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testRepeatingLazyColumn() {
        val title = "#same"
        val count = 3
        val root = Identifier.ROOT
        composeTestRule.setContent {
            RepeatingLazyColumn(
                modifier = Modifier.testIdentifier(root),
                itemCount = count,
                content = {
                    Text(
                        modifier = Modifier
                            .testIdentifier(Identifier.CONTENT),
                        text = title
                    )
                }
            )
        }

        composeTestRule.onNodeWithIdentifier(root).assertIsDisplayed()
        composeTestRule.onAllNodes(Identifier.CONTENT).assertCountEquals(count)
    }

    private enum class Identifier {
        ROOT, CONTENT
    }
}
