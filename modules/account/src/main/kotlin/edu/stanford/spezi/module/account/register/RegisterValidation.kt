package edu.stanford.spezi.module.account.register

import java.time.LocalDate

fun isEmailValid(email: String): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun isPasswordValid(password: String): Boolean =
    password.length >= 6 // Minimum for firebase

fun isFirstNameValid(firstName: String): Boolean =
    firstName.isNotEmpty()

fun isLastNameValid(lastName: String): Boolean =
    lastName.isNotEmpty()

fun isGenderValid(gender: String): Boolean =
    listOf("Male", "Female", "Other").contains(gender)

fun isDobValid(dateOfBirth: LocalDate?): Boolean =
    dateOfBirth != null && dateOfBirth.isBefore(LocalDate.now())

fun isFormValid(uiState: RegisterUiState): Boolean =
    isEmailValid(uiState.email.value)
            && isPasswordValid(uiState.password.value)
            && isFirstNameValid(uiState.firstName.value)
            && isLastNameValid(uiState.lastName.value)
            && isGenderValid(uiState.selectedGender.value)
            && isDobValid(uiState.dateOfBirth)
