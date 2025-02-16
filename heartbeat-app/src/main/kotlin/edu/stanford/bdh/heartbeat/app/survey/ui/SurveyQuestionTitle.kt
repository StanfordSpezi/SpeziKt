package edu.stanford.bdh.heartbeat.app.survey.ui

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView


data class QuestionTitle(
    val content: String,
) : SurveyItem {
    @Composable
    override fun Content(modifier: Modifier) {
        SurveyCard {
            val isHtml = remember(content) { isHtml(content) }
            if (isHtml) {
                HtmlText(modifier)
            } else {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = content.trim(),
                    textAlign = TextAlign.Start
                )
            }
        }
    }

    /**
     * TODO: Apply theme colors, font family and correct sizes
     */
    @Composable
    private fun styled(): String = """
    <!doctype html>
    <html lang="en">
    <head>
        <meta charset="utf-8">
        <style type="text/css">
            /* General body styling */
            body {
                font-family: sans-serif;
                color: black;
                line-height: 1.6;
                background-color: transparent;
                margin: 0;
                padding: 0;
            }
            /* Headings */
            h1, h2, h3, h4, h5, h6 {
                font-weight: bold;
                margin: 1em 0;
            }
            h1 { font-size: 2em; }
            h2 { font-size: 1.75em; }
            h3 { font-size: 1.5em; }
            h4 { font-size: 1.25em; }
            h5 { font-size: 1.1em; }
            h6 { font-size: 1em; }
            /* Inline elements */
            b, strong { font-weight: bold; }
            i, em { font-style: italic; }
            u { text-decoration: underline; }
            sup { vertical-align: super; font-size: smaller; }
            /* Links */
            a {
                color: #008000; /* System green */
                text-decoration: none;
            }
            a:hover {
                text-decoration: underline;
            }
            /* Lists */
            ul, ol {
                margin: 1em 0;
                padding-left: 2em;
            }
            li {
                margin: 0.5em 0;
            }
            li:last-child {
                margin-bottom: 1em;
            }
            /* Paragraphs */
            p {
                margin: 0;
                color: black;
            }
            /* Horizontal rule */
            hr {
                border: none;
                border-top: 1px solid #d3d3d3; /* Light gray separator */
                margin: 1em 0;
            }
            /* Breaks */
            br {
                content: "";
                display: block;
                margin: 0.5em 0;
            }
            /* Images */
            img {
                max-width: 100%;
                height: auto;
                margin: 1em 0;
            }
            /* Div and Span */
            span, div {
                display: block;
                margin: 0.5em 0;
            }
        </style>
    </head>
    <body>
        $content
    </body>
    </html>
""".trimIndent()


    @Composable
    private fun HtmlText(modifier: Modifier) {
        val styledContent = styled()
        AndroidView(
            modifier = modifier,
            factory = { context ->
                WebView(context).apply {
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                    loadDataWithBaseURL(
                        null,
                        styledContent,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            }
        )
    }

    private companion object {
        private val htmlRegex =
            "<([a-zA-Z0-9]+)(?:\\s+[^>]*)?>.*?</\\1>|<([a-zA-Z0-9]+)(?:\\s+[^>]*)?/?>".toRegex()

        fun isHtml(text: String): Boolean {
            return htmlRegex.containsMatchIn(text)
        }
    }
}