package edu.stanford.bdh.engagehf.phonenumber

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import edu.stanford.bdh.engagehf.R
import edu.stanford.spezi.ui.BottomSheetComposableContent
import edu.stanford.spezi.ui.ComposableBlock
import edu.stanford.spezi.ui.disabledAlpha
import edu.stanford.spezi.ui.theme.Colors
import edu.stanford.spezi.ui.theme.Spacings
import edu.stanford.spezi.ui.theme.SpeziTheme
import edu.stanford.spezi.ui.theme.ThemePreviews

@OptIn(ExperimentalMaterial3Api::class)
data class CountryCodeSelectionUiModel(
    val searchQuery: String,
    val onSearchQueryChanged: (String) -> Unit,
    val items: List<CountryCodeUiModel>,
    override val onDismiss: () -> Unit,
) : BottomSheetComposableContent {

    override val skipPartiallyExpanded: Boolean = true
    override val dragHandle: ComposableBlock = { BottomSheetDefaults.DragHandle() }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        val keyboardController = LocalSoftwareKeyboardController.current
        LazyColumn(modifier.padding(Spacings.medium)) {
            stickyHeader {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(color = Colors.secondary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Colors.surface),
                    decorationBox = @Composable { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Spacings.small),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    modifier = Modifier.disabledAlpha(),
                                    text = stringResource(R.string.search_your_country_placeholder),
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.disabledAlpha())
            }

            if (items.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_country_codes_found),
                        modifier = Modifier.padding(top = Spacings.small)
                    )
                }
            } else {
                itemsIndexed(items) { index, item ->
                    if (index > 0) HorizontalDivider(modifier = Modifier.disabledAlpha())
                    item.Content()
                }
            }
        }
    }
}

class CountryCodePreviewParamProvider : PreviewParameterProvider<CountryCodeSelectionUiModel> {
    private val base = CountryCodeSelectionUiModel(
        searchQuery = "",
        onSearchQueryChanged = {},
        items = listOf(
            CountryCodeUiModel(
                emojiFlag = "🇺🇸",
                countryCode = "US",
                countryName = "United States",
                number = "+1",
                onClick = {},
            ),
            CountryCodeUiModel(
                emojiFlag = "🇩🇪",
                countryCode = "DE",
                countryName = "Germany",
                number = "+49",
                onClick = {},
            ),
        ),
        onDismiss = {}
    )

    override val values: Sequence<CountryCodeSelectionUiModel>
        get() = sequenceOf(
            base,
            base.copy(searchQuery = "germ", items = listOf(base.items.last())),
            base.copy(searchQuery = "asdf", items = emptyList()),
        )
}

@ThemePreviews
@Composable
private fun Preview(@PreviewParameter(CountryCodePreviewParamProvider::class) model: CountryCodeSelectionUiModel) {
    SpeziTheme { model.Content() }
}
