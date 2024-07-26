package edu.stanford.bdh.engagehf.health.weight.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles
import edu.stanford.spezi.core.design.theme.ThemePreviews
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AddWeightBottomSheet() {
    val viewModel = hiltViewModel<AddWeightBottomSheetViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    AddWeightBottomSheet(
        uiState = uiState, onAction = viewModel::onAction
    )
}

@Composable
fun AddWeightBottomSheet(
    uiState: AddWeightBottomSheetViewModel.UiState,
    onAction: (AddWeightBottomSheetViewModel.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacings.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        when (uiState.currentStep) {
            AddWeightBottomSheetViewModel.Step.WEIGHT -> EnterWeightStep(
                uiState,
                onAction,
            )

            AddWeightBottomSheetViewModel.Step.DATE -> EnterDateStep(
                uiState,
                onAction,
            )

            AddWeightBottomSheetViewModel.Step.TIME -> EnterTimeStep(
                uiState,
                onAction,
            )

            AddWeightBottomSheetViewModel.Step.REVIEW -> ReviewStep(
                uiState,
                onAction,
            )
        }
    }
}

@Composable
private fun ReviewStep(
    uiState: AddWeightBottomSheetViewModel.UiState,
    onAction: (AddWeightBottomSheetViewModel.Action) -> Unit,
) {
    Text(text = "Review Details", style = TextStyles.titleLarge)
    VerticalSpacer()

    OutlinedTextField(value = if (uiState.weight != null) uiState.weight.toString() else "",
        onValueChange = {},
        label = { Text("Weight") },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {
                onAction(
                    AddWeightBottomSheetViewModel.Action.UpdateCurrentStep(
                        AddWeightBottomSheetViewModel.Step.WEIGHT
                    )
                )
            }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
        })

    OutlinedTextField(value = uiState.date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
        onValueChange = {},
        label = { Text("Date") },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {
                onAction(
                    AddWeightBottomSheetViewModel.Action.UpdateCurrentStep(
                        AddWeightBottomSheetViewModel.Step.DATE
                    )
                )
            }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
        })

    OutlinedTextField(value = uiState.date.format(DateTimeFormatter.ofPattern("HH:mm")),
        onValueChange = {},
        label = { Text("Time") },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = {
                onAction(
                    AddWeightBottomSheetViewModel.Action.UpdateCurrentStep(
                        AddWeightBottomSheetViewModel.Step.TIME
                    )
                )
            }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
        })

    VerticalSpacer()
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FilledTonalButton(
            onClick = {
                onAction(
                    AddWeightBottomSheetViewModel.Action.UpdateCurrentStep(
                        AddWeightBottomSheetViewModel.Step.TIME
                    )
                )
            }, modifier = Modifier
                .weight(1f)
                .padding(end = Spacings.small)
        ) {
            Text(text = stringResource(id = com.google.android.fhir.datacapture.R.string.button_pagination_previous))
        }
        Button(
            onClick = {
                onAction(
                    AddWeightBottomSheetViewModel.Action.SaveWeight
                )
            }, modifier = Modifier
                .padding(start = Spacings.small)
                .weight(1f)
        ) {
            Text(text = stringResource(id = com.google.android.fhir.datacapture.R.string.save))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EnterTimeStep(
    uiState: AddWeightBottomSheetViewModel.UiState,
    onAction: (AddWeightBottomSheetViewModel.Action) -> Unit,
) {
    Text(text = "Select Time", style = TextStyles.titleLarge)
    VerticalSpacer()
    val rememberTimePickerState = rememberTimePickerState(
        initialHour = uiState.hour, initialMinute = uiState.minute
    )
    TimePicker(state = rememberTimePickerState)
    VerticalSpacer()
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FilledTonalButton(
            onClick = {
                onAction(
                    AddWeightBottomSheetViewModel.Action.UpdateCurrentStep(
                        AddWeightBottomSheetViewModel.Step.DATE
                    )
                )
            }, modifier = Modifier
                .weight(1f)
                .padding(end = Spacings.small)
        ) {
            Text(text = stringResource(id = com.google.android.fhir.datacapture.R.string.button_pagination_previous))
        }
        Button(
            onClick = {
                onAction(
                    AddWeightBottomSheetViewModel.Action.UpdateTime(
                        hour = rememberTimePickerState.hour, minute = rememberTimePickerState.minute
                    )
                )
                onAction(
                    AddWeightBottomSheetViewModel.Action.UpdateCurrentStep(
                        AddWeightBottomSheetViewModel.Step.REVIEW
                    )
                )
            }, modifier = Modifier
                .weight(1f)
                .padding(start = Spacings.small)
        ) {
            Text(
                text = stringResource(id = com.google.android.fhir.datacapture.R.string.button_pagination_next)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class DatesUpToToday : SelectableDates {
    private val today = System.currentTimeMillis()

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis <= today
    }

    override fun isSelectableYear(year: Int): Boolean {
        return year <= ZonedDateTime.now().year
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EnterDateStep(
    uiState: AddWeightBottomSheetViewModel.UiState,
    onAction: (AddWeightBottomSheetViewModel.Action) -> Unit,
) {
    Text(text = "Select Date", style = TextStyles.titleLarge)
    VerticalSpacer()

    val rememberDatePickerState = rememberDatePickerState(
        yearRange = 2024..ZonedDateTime.now().year,
        selectableDates = DatesUpToToday(),
        initialSelectedDateMillis = uiState.selectedDateMillis
    )
    DatePicker(state = rememberDatePickerState)
    VerticalSpacer()
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        FilledTonalButton(
            onClick = {
                onAction(
                    AddWeightBottomSheetViewModel.Action.UpdateCurrentStep(
                        AddWeightBottomSheetViewModel.Step.WEIGHT
                    )
                )
            }, modifier = Modifier
                .weight(1f)
                .padding(end = Spacings.small)
        ) {
            Text(text = stringResource(id = com.google.android.fhir.datacapture.R.string.button_pagination_previous))
        }
        Button(
            onClick = {
                rememberDatePickerState.selectedDateMillis?.let {
                    onAction(
                        AddWeightBottomSheetViewModel.Action.UpdateDate(
                            it
                        )
                    )
                    onAction(
                        AddWeightBottomSheetViewModel.Action.UpdateCurrentStep(
                            AddWeightBottomSheetViewModel.Step.TIME
                        )
                    )
                }
            }, modifier = Modifier
                .weight(1f)
                .padding(start = Spacings.small)
        ) {
            Text(text = stringResource(id = com.google.android.fhir.datacapture.R.string.button_pagination_next))
        }
    }
}

@Composable
private fun EnterWeightStep(
    uiState: AddWeightBottomSheetViewModel.UiState,
    onAction: (AddWeightBottomSheetViewModel.Action) -> Unit,
) {
    Text(text = "Enter Weight", style = TextStyles.titleLarge)
    VerticalSpacer()
    OutlinedTextField(
        value = if (uiState.weight != null) uiState.weight.toString() else "",
        onValueChange = {
            it.toDoubleOrNull()?.let { weight ->
                onAction(
                    AddWeightBottomSheetViewModel.Action.UpdateWeight(
                        weight
                    )
                )
            }
        },
        label = { Text("Weight") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

    )
    VerticalSpacer()
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                uiState.weight?.let {
                    onAction(
                        AddWeightBottomSheetViewModel.Action.UpdateCurrentStep(
                            AddWeightBottomSheetViewModel.Step.DATE
                        )
                    )
                }
            }, modifier = Modifier.weight(1f)
        ) {
            Text(text = stringResource(id = com.google.android.fhir.datacapture.R.string.button_pagination_next))
        }
    }
}

@ThemePreviews
@Composable
fun AddWeightBottomSheetPreview(
    @PreviewParameter(AddWeightBottomSheetStepProvider::class) uiState: AddWeightBottomSheetViewModel.UiState,
) {
    SpeziTheme(isPreview = true) {
        AddWeightBottomSheet(uiState = uiState, onAction = {})
    }
}

private class AddWeightBottomSheetStepProvider :
    PreviewParameterProvider<AddWeightBottomSheetViewModel.UiState> {
    override val values: Sequence<AddWeightBottomSheetViewModel.UiState>
        get() = sequenceOf(
            AddWeightBottomSheetViewModel.UiState(
                currentStep = AddWeightBottomSheetViewModel.Step.WEIGHT, weight = 70.0
            ), AddWeightBottomSheetViewModel.UiState(
                currentStep = AddWeightBottomSheetViewModel.Step.DATE,
            ), AddWeightBottomSheetViewModel.UiState(
                currentStep = AddWeightBottomSheetViewModel.Step.TIME,
            ), AddWeightBottomSheetViewModel.UiState(
                currentStep = AddWeightBottomSheetViewModel.Step.REVIEW,
                weight = 70.0,
            )
        )
}
