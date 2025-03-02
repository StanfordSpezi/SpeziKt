package edu.stanford.bdh.heartbeat.app.survey.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import edu.stanford.spezi.core.design.theme.ThemePreviews

data class SurveyQuestionTitle(
    private val content: String,
) : SurveyItem {

    @Composable
    override fun Body(modifier: Modifier) {
        SurveyCard {
            val isHtml = remember(content) { HtmlUtils.isHtml(content) }
            if (isHtml) {
                HtmlText(text = content, modifier = modifier)
            } else {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = content.trim(),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@ThemePreviews
@Composable
fun ProgressPreview() {
    val lipsum = """
        lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua ut enim ad minim veniam quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur excepteur sint occaecat cupidatat non proident sunt in culpa qui officia deserunt mollit anim id est laborum
    """.trimIndent()
    SurveyItemPreview {
        SurveyQuestionTitle(lipsum).body
    }
}
