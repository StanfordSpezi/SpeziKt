package edu.stanford.spezi.module.account.manager

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.credentials.CredentialManager
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import edu.stanford.spezi.core.testing.mockTask
import edu.stanford.spezi.core.testing.runTestUnconfined
import edu.stanford.spezi.module.account.register.GenderIdentity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class AuthenticationManagerTest {
    private val googleIdToken = "test-google-id-token"
    private val firstName = "first name"
    private val lastName = "last name"
    private val email = "email@email.com"
    private val password = "password"
    private val gender = GenderIdentity.PREFER_NOT_TO_STATE
    private val selectedGender: String
        get() = gender.displayName
    private val firebaseUserUid = "uid"
    private val dateOfBirth: LocalDate = LocalDate.of(2024, 1, 1)
    private val firebaseUser: FirebaseUser = mockk {
        every { uid } returns firebaseUserUid
    }
    private val authResult: AuthResult = mockk {
        every { user } returns firebaseUser
    }
    private val noUserAuthResult: AuthResult = mockk {
        every { user } returns null
    }
    private val credential: AuthCredential = mockk()
    private val documentReference: DocumentReference = mockk()

    private val firebaseAuth: FirebaseAuth = mockk()
    private val firestore: FirebaseFirestore = mockk()
    private val credentialManager: CredentialManager = mockk()
    private val context: Context = mockk()

    private val authenticationManager = AuthenticationManager(
        firebaseAuth = firebaseAuth,
        firestore = firestore,
        credentialManager = credentialManager,
        context = context
    )

    @Before
    fun setup() {
        mockkStatic(TextUtils::class)
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()
        every { TextUtils.isEmpty(any()) } returns false
        mockkStatic(GoogleAuthProvider::class)
        coEvery {
            firestore.collection("users").document(firebaseUserUid)
        } returns documentReference
    }

    @After
    fun tearDown() {
        unmockkStatic(TextUtils::class)
        unmockkStatic(Uri::class)
        unmockkStatic(GoogleAuthProvider::class)
    }

    @Test
    fun `signUpWithGoogleAccount should create user and save user data`() = runTestUnconfined {
        // given
        every { firebaseAuth.currentUser } returns firebaseUser
        every { GoogleAuthProvider.getCredential(googleIdToken, null) } returns credential
        coEvery { firebaseAuth.signInWithCredential(credential) } returns mockTask(authResult)
        coEvery { firebaseUser.updateProfile(any<UserProfileChangeRequest>()) } returns mockTask(mockk())
        coEvery { firebaseUser.updateEmail(email) } returns mockTask(mockk())
        val mapCaptor = slot<Map<String, Any>>()
        every { documentReference.set(capture(mapCaptor)) } returns mockTask(mockk())

        // when
        val result = authenticationManager.signUpWithGoogleAccount(
            googleIdToken = googleIdToken,
            firstName = firstName,
            lastName = lastName,
            email = email,
            selectedGender = selectedGender,
            dateOfBirth = dateOfBirth
        )

        // then
        coVerify {
            firebaseAuth.signInWithCredential(credential)
            firebaseUser.updateProfile(any<UserProfileChangeRequest>())
            firebaseUser.updateEmail(email)
            documentReference.set(any())
        }
        with(mapCaptor.captured) {
            assertThat(get("GenderIdentityKey")).isEqualTo(gender.databaseName)
            assertThat(get("DateOfBirthKey")).isNotNull()
        }
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `signUpWithGoogleAccount should return error if signInWithCredential fails to return user`() =
        runTestUnconfined {
            // given
            every { GoogleAuthProvider.getCredential(googleIdToken, null) } returns credential
            coEvery { firebaseAuth.signInWithCredential(credential) } returns mockTask(
                noUserAuthResult
            )

            // when
            val result = authenticationManager.signUpWithGoogleAccount(
                googleIdToken = googleIdToken,
                firstName = firstName,
                lastName = lastName,
                email = email,
                selectedGender = selectedGender,
                dateOfBirth = dateOfBirth
            )

            // then
            coVerify {
                firebaseAuth.signInWithCredential(credential)
            }
            assertThat(result.isFailure).isTrue()
        }

    @Test
    fun `signUpWithGoogleAccount should return error if creating fails`() = runTestUnconfined {
        // given
        every { firebaseAuth.currentUser } returns firebaseUser
        every { GoogleAuthProvider.getCredential(googleIdToken, null) } returns credential
        coEvery { firebaseUser.linkWithCredential(credential) } throws Exception("Failed to create")

        // when
        val result = authenticationManager.signUpWithGoogleAccount(
            googleIdToken = googleIdToken,
            firstName = firstName,
            lastName = lastName,
            email = email,
            selectedGender = selectedGender,
            dateOfBirth = dateOfBirth
        )

        // then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `signUpWithEmailAndPassword should sign up user and save user data`() = runTestUnconfined {
        // given
        coEvery {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
        } returns mockTask(authResult)
        every { firebaseAuth.currentUser } returns firebaseUser
        coEvery { firebaseUser.updateProfile(any<UserProfileChangeRequest>()) } returns mockTask(mockk())
        coEvery { firebaseUser.updateEmail(email) } returns mockTask(mockk())
        val mapCaptor = slot<Map<String, Any>>()
        every { documentReference.set(capture(mapCaptor)) } returns mockTask(mockk())

        // when
        val result = authenticationManager.signUpWithEmailAndPassword(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            selectedGender = selectedGender,
            dateOfBirth = dateOfBirth
        )

        // then
        coVerify {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
            firebaseUser.updateProfile(any<UserProfileChangeRequest>())
            firebaseUser.updateEmail(email)
            documentReference.set(any())
        }
        with(mapCaptor.captured) {
            assertThat(get("GenderIdentityKey")).isEqualTo(gender.databaseName)
            assertThat(get("DateOfBirthKey")).isNotNull()
        }
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `signUpWithEmailAndPassword should return error if sign up returns null user`() = runTestUnconfined {
        // given
        coEvery {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
        } returns mockTask(noUserAuthResult)

        // when
        val result = authenticationManager.signUpWithEmailAndPassword(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            selectedGender = selectedGender,
            dateOfBirth = dateOfBirth
        )

        // then
        coVerify {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
        }
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `signUpWithEmailAndPassword should return error if sign up fails`() = runTestUnconfined {
        // given
        coEvery {
            firebaseAuth.createUserWithEmailAndPassword(
                email,
                password
            )
        } throws Exception("Sign up failed")

        // when
        val result = authenticationManager.signUpWithEmailAndPassword(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            selectedGender = selectedGender,
            dateOfBirth = dateOfBirth
        )

        // then
        coVerify { firebaseAuth.createUserWithEmailAndPassword(email, password) }
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `it should handle successful send and forget password correctly`() = runTestUnconfined {
        // given
        coEvery { firebaseAuth.sendPasswordResetEmail(email) } returns mockTask(mockk<Void>())

        // when
        val result = authenticationManager.sendForgotPasswordEmail(email)

        // then
        coVerify { firebaseAuth.sendPasswordResetEmail(email) }
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `it should handle failure send and forget password correctly`() = runTestUnconfined {
        // given
        coEvery { firebaseAuth.sendPasswordResetEmail(email) } throws Exception("Failure")

        // when
        val result = authenticationManager.sendForgotPasswordEmail(email)

        // then
        coVerify { firebaseAuth.sendPasswordResetEmail(email) }
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `it should sign in user with email and password`() = runTestUnconfined {
        // given
        coEvery {
            firebaseAuth.signInWithEmailAndPassword(
                email,
                password
            )
        } returns mockTask(authResult)

        // when
        val result = authenticationManager.signIn(
            email = email,
            password = password
        )

        // then
        coVerify { firebaseAuth.signInWithEmailAndPassword(email, password) }
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `it should return error if sign in fails`() = runTestUnconfined {
        // given
        coEvery {
            firebaseAuth.signInWithEmailAndPassword(email, password)
        } throws Exception("Sign in failed")

        // when
        val result = authenticationManager.signIn(
            email = email,
            password = password
        )

        // then
        coVerify { firebaseAuth.signInWithEmailAndPassword(email, password) }
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `it should return error if sign in returns null user fails`() = runTestUnconfined {
        // given
        coEvery {
            firebaseAuth.signInWithEmailAndPassword(email, password)
        } returns mockTask(noUserAuthResult)

        // when
        val result = authenticationManager.signIn(
            email = email,
            password = password
        )

        // then
        coVerify { firebaseAuth.signInWithEmailAndPassword(email, password) }
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `it should sign in user with Google credential`() = runTestUnconfined {
        // given
        val manager = spyk(authenticationManager)
        val googleIdTokenCredential: GoogleIdTokenCredential = mockk {
            every { idToken } returns googleIdToken
        }
        coEvery { manager.getCredential(filterByAuthorizedAccounts = true) } returns googleIdTokenCredential
        every { GoogleAuthProvider.getCredential(googleIdToken, null) } returns credential
        coEvery { firebaseAuth.signInWithCredential(credential) } returns mockTask(authResult)

        // when
        val result = manager.signInWithGoogle()

        // then
        coVerify { firebaseAuth.signInWithCredential(credential) }
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `it should return error if sign in with google returns null user`() = runTestUnconfined {
        // given
        val manager = spyk(authenticationManager)
        val googleIdTokenCredential: GoogleIdTokenCredential = mockk {
            every { idToken } returns googleIdToken
        }

        coEvery { manager.getCredential(filterByAuthorizedAccounts = true) } returns googleIdTokenCredential
        every { GoogleAuthProvider.getCredential(googleIdToken, null) } returns credential
        coEvery { firebaseAuth.signInWithCredential(credential) } returns mockTask(noUserAuthResult)

        // when
        val result = manager.signInWithGoogle()

        // then
        coVerify { firebaseAuth.signInWithCredential(credential) }
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `it should return error if sign in with google fails`() = runTestUnconfined {
        // given
        val manager = spyk(authenticationManager)
        coEvery { manager.getCredential(filterByAuthorizedAccounts = true) } returns null

        // when
        val result = manager.signInWithGoogle()

        // then
        coVerify { manager.getCredential(filterByAuthorizedAccounts = true) }
        assertThat(result.isFailure).isTrue()
    }
}
