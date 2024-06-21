package edu.stanford.spezi.module.account.register

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import edu.stanford.spezi.core.design.theme.TextStyles.titleLarge
import edu.stanford.spezi.core.design.theme.TextStyles.titleSmall
import java.time.LocalDate

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

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onAction: (Action) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val genderFocus = remember { FocusRequester() }
    val dateOfBirthFocus = remember { FocusRequester() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacings.medium)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        keyboardController?.hide()
                    }
                )
            },
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
        IconLeadingContent(
            icon = Icons.Outlined.Email,
            content = {
                ValidatedOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.email.value,
                    onValueChange = {
                        onAction(Action.TextFieldUpdate(it, TextFieldType.EMAIL))
                    },
                    labelText = "E-Mail Address",
                    errorText = uiState.email.error,
                )
            })
        VerticalSpacer(height = Spacings.small)
        if (!uiState.isGoogleSignUp) {
            IconLeadingContent(
                icon = Icons.Outlined.Lock,
                content = {
                    ValidatedOutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = uiState.password.value,
                        onValueChange = {
                            onAction(Action.TextFieldUpdate(it, TextFieldType.PASSWORD))
                        },
                        labelText = "Password",
                        errorText = uiState.password.error,
                        trailingIcon = {
                            IconButton(onClick = { onAction(Action.TogglePasswordVisibility) }) {
                                val iconId: Int = if (uiState.isPasswordVisible) {
                                    edu.stanford.spezi.core.design.R.drawable.ic_visibility
                                } else {
                                    edu.stanford.spezi.core.design.R.drawable.ic_visibility_off
                                }
                                Icon(
                                    painter = painterResource(id = iconId),
                                    contentDescription = if (uiState.isPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (uiState.isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                    )
                })
        }
        IconLeadingContent(
            icon = Icons.Outlined.Person,
            content = {
                ValidatedOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.firstName.value,
                    onValueChange = {
                        onAction(
                            Action.TextFieldUpdate(
                                it,
                                TextFieldType.FIRST_NAME
                            )
                        )
                    },
                    labelText = "First Name",
                    errorText = uiState.firstName.error,
                )
            })
        VerticalSpacer(height = Spacings.small)
        IconLeadingContent(
            content = {
                ValidatedOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.lastName.value,
                    onValueChange = {
                        onAction(Action.TextFieldUpdate(it, TextFieldType.LAST_NAME))
                    },
                    labelText = "Last Name",
                    errorText = uiState.lastName.error,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        genderFocus.requestFocus()
                        if (uiState.selectedGender.value.isEmpty()) {
                            onAction(Action.DropdownMenuExpandedUpdate(true))
                        }
                    })
                )
            })
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = uiState.isDropdownMenuExpanded,
            onDismissRequest = {
                onAction(Action.DropdownMenuExpandedUpdate(false))
                dateOfBirthFocus.requestFocus()
            }
        ) {
            uiState.genderOptions.forEach { gender ->
                DropdownMenuItem(text = { Text(text = gender) },
                    onClick = {
                        onAction(Action.TextFieldUpdate(gender, TextFieldType.GENDER))
                        onAction(Action.DropdownMenuExpandedUpdate(false))
                        dateOfBirthFocus.requestFocus()
                    })
            }
        }
        IconLeadingContent(
            content = {
                ValidatedOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(genderFocus),
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
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Select Gender",
                            )
                        }
                    },
                    errorText = uiState.selectedGender.error,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        dateOfBirthFocus.requestFocus()
                        if (uiState.selectedGender.value.isNotEmpty()) {
                            onAction(Action.SetIsDatePickerOpen(true))
                        }
                    }),
                )
            })

        VerticalSpacer(height = Spacings.small)
        IconLeadingContent(
            content = {
                ValidatedOutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(dateOfBirthFocus),
                    value = uiState.formattedDateOfBirth,
                    onValueChange = { /* Do nothing as we handle the date through the DatePicker */ },
                    labelText = "Date of Birth",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    trailingIcon = {
                        IconButton(onClick = {
                            onAction(Action.SetIsDatePickerOpen(true))
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Select Date")
                        }
                    },
                    readOnly = true,
                    keyboardActions = KeyboardActions(onDone = {
                        onAction(Action.OnRegisterPressed)
                    })
                )
            })

        if (uiState.isDatePickerDialogOpen) {
            DatePickerDialog(
                onDateSelected = { date ->
                    onAction(Action.DateFieldUpdate(date))
                    onAction(Action.SetIsDatePickerOpen(false))
                },
                onDismiss = {
                    onAction(Action.SetIsDatePickerOpen(false))
                }
            )
        }

        Spacer(modifier = Modifier.height(Spacings.large))
        Button(
            onClick = {
                onAction(Action.OnRegisterPressed)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.email.value.isNotEmpty() &&
                uiState.firstName.value.isNotEmpty() &&
                uiState.lastName.value.isNotEmpty() &&
                uiState.selectedGender.value.isNotEmpty() &&
                uiState.dateOfBirth != null
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
