package edu.stanford.spezi.module.account.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import edu.stanford.spezi.core.design.component.VerticalSpacer
import edu.stanford.spezi.core.design.component.validated.outlinedtextfield.ValidatedOutlinedTextField
import edu.stanford.spezi.core.design.theme.Colors.primary
import edu.stanford.spezi.core.design.theme.Sizes
import edu.stanford.spezi.core.design.theme.Spacings
import edu.stanford.spezi.core.design.theme.SpeziTheme
import edu.stanford.spezi.core.design.theme.TextStyles.labelLarge
import edu.stanford.spezi.core.design.theme.TextStyles.titleLarge
import edu.stanford.spezi.core.design.theme.TextStyles.titleSmall
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun RegisterScreen(
    isGoogleSignUp: Boolean,
    email: String,
    password: String,
) {
    val viewModel = hiltViewModel<RegisterViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(key1 = isGoogleSignUp, key2 = email, key3 = password) {
        viewModel.onAction(Action.SetIsGoogleSignUp(isGoogleSignUp))
        viewModel.onAction(Action.TextFieldUpdate(email, TextFieldType.EMAIL))
        viewModel.onAction(Action.TextFieldUpdate(password, TextFieldType.PASSWORD))
    }

    RegisterScreen(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onAction: (Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacings.medium)
            .imePadding()
            .imeNestedScroll()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Create a new Account Icon",
            tint = primary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(Sizes.Icon.large)
        )
        Text(
            text = "Create a new Account",
            style = titleLarge,
        )
        VerticalSpacer()
        Text(
            "Please fill out the details below to create your new account.",
            style = titleSmall,
        )
        VerticalSpacer(height = Spacings.large)
        Text("CREDENTIALS", style = labelLarge)
        ValidatedOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.email.value,
            onValueChange = {
                onAction(Action.TextFieldUpdate(it, TextFieldType.EMAIL))
            },
            labelText = "E-Mail Address",
            errorText = uiState.email.error,
        )
        VerticalSpacer(height = Spacings.small)
        if (!uiState.isGoogleSignUp) {
            ValidatedOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.password.value,
                onValueChange = {
                    onAction(Action.TextFieldUpdate(it, TextFieldType.PASSWORD))
                },
                labelText = "Password",
                errorText = uiState.password.error,
                visualTransformation = PasswordVisualTransformation(),
            )
        }
        VerticalSpacer()
        Text("NAME", style = labelLarge)
        ValidatedOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.firstName.value,
            onValueChange = { onAction(Action.TextFieldUpdate(it, TextFieldType.FIRST_NAME)) },
            labelText = "First Name",
            errorText = uiState.firstName.error,
        )
        VerticalSpacer(height = Spacings.small)
        ValidatedOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.lastName.value,
            onValueChange = {
                onAction(Action.TextFieldUpdate(it, TextFieldType.LAST_NAME))
            },
            labelText = "Last Name",
            errorText = uiState.lastName.error,
        )
        VerticalSpacer()
        Text("PERSONAL DETAILS", style = labelLarge)
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = uiState.isDropdownMenuExpanded,
            onDismissRequest = {
                onAction(Action.DropdownMenuExpandedUpdate(false))
            }
        ) {
            uiState.genderOptions.forEach { gender ->
                DropdownMenuItem(text = { Text(text = gender) },
                    onClick = {
                        onAction(Action.TextFieldUpdate(gender, TextFieldType.GENDER))
                        onAction(Action.DropdownMenuExpandedUpdate(false))
                    })
            }
        }

        ValidatedOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.selectedGender.value,
            onValueChange = {
                onAction(Action.TextFieldUpdate(it, TextFieldType.GENDER))
            },
            labelText = "Gender",
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    onAction(Action.DropdownMenuExpandedUpdate(true))
                }) {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Select Gender")
                }
            },
            errorText = uiState.selectedGender.error,
        )

        VerticalSpacer(height = Spacings.small)
        var isDatePickerDialogOpen by remember { mutableStateOf(false) }
        ValidatedOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.dateOfBirth?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) ?: "",
            onValueChange = { /* Do nothing as we handle the date through the DatePicker */ },
            labelText = "Date of Birth",
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            trailingIcon = {
                IconButton(onClick = { isDatePickerDialogOpen = true }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Select Date")
                }
            },
            readOnly = true
        )

        if (isDatePickerDialogOpen) {
            DatePickerDialog(
                onDateSelected = { date ->
                    onAction(Action.DateFieldUpdate(date))
                    isDatePickerDialogOpen = false
                },
                onDismiss = { isDatePickerDialogOpen = false }
            )
        }

        Spacer(modifier = Modifier.height(Spacings.large))
        Button(
            onClick = {
                onAction(Action.OnRegisterPressed)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.isFormValid
        ) {
            Text("Signup")
        }
    }
}

@Preview
@Composable
private fun RegisterScreenPreview(
    @PreviewParameter(RegisterScreenProvider::class) uiState: RegisterUiState,
) {
    SpeziTheme {
        RegisterScreen(
            uiState = uiState,
            onAction = { }
        )
    }
}

private class RegisterScreenProvider : PreviewParameterProvider<RegisterUiState> {
    override val values: Sequence<RegisterUiState> = sequenceOf(
        RegisterUiState(
            email = FieldState("", null),
            password = FieldState("", null),
            firstName = FieldState("", null),
            lastName = FieldState("", null),
            selectedGender = FieldState("", null),
            dateOfBirth = null,
            isFormValid = false
        ),
        RegisterUiState(
            email = FieldState("test@test.de", null),
            password = FieldState("password", null),
            isGoogleSignUp = true,
            firstName = FieldState("John", null),
            lastName = FieldState("Doe", null),
            selectedGender = FieldState("Male", null),
            dateOfBirth = LocalDate.now(),
            isFormValid = true
        ),
        RegisterUiState(
            email = FieldState("test", "Invalid email"),
            password = FieldState("pass", "Password to short"),
            firstName = FieldState("John", null),
            lastName = FieldState("Doe", null),
            selectedGender = FieldState("Male", null),
            dateOfBirth = LocalDate.now(),
            isFormValid = false,
        ),
    )
}
