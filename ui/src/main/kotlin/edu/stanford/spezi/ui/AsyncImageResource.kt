package edu.stanford.spezi.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage

/**
 * Composable function to display an image from a remote url
 */
@Composable
fun AsyncImageResource(
    url: String?,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    loading: @Composable BoxScope.() -> Unit = { },
    error: @Composable BoxScope.() -> Unit = { },
) {
    SubcomposeAsyncImage(
        model = url,
        modifier = modifier,
        contentDescription = contentDescription,
        success = {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painter,
                contentDescription = contentDescription,
                contentScale = contentScale,
            )
        },
        loading = {
            Box(
                modifier = Modifier.fillMaxSize(),
                content = loading,
            )
        },
        error = {
            Box(
                modifier = Modifier.fillMaxSize(),
                content = error,
            )
        }
    )
}
