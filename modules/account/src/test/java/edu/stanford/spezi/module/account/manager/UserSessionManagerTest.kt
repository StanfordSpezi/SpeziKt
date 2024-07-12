package edu.stanford.spezi.module.account.manager

import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import edu.stanford.spezi.core.testing.SpeziTestScope
import edu.stanford.spezi.core.testing.runTestUnconfined
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test

class UserSessionManagerTest {
    private val firebaseStorage: FirebaseStorage = mockk()
    private val firebaseAuth: FirebaseAuth = mockk()

    private lateinit var authStateListener: AuthStateListener
    private lateinit var userSessionManager: UserSessionManager

    @Test
    fun `it should register auth state listener on init`() {
        // when
        createUserSessionManager()

        // then
        verify { firebaseAuth.addAuthStateListener(any()) }
    }

    @Test
    fun `it should have not initialized user state on init if no listener callback is received`() {
        // given
        createUserSessionManager()

        // when
        val state = userSessionManager.userState.value

        // then
        assertThat(state).isEqualTo(UserState.NotInitialized)
    }

    @Test
    fun `it should reflect not updated user state when invoked with null user callback`() =
        runTestUnconfined {
            // given
            every { firebaseAuth.currentUser } returns null
            createUserSessionManager()

            // when
            authStateListener.onAuthStateChanged(firebaseAuth)

            // then
            assertThat(userSessionManager.userState.value).isEqualTo(UserState.NotInitialized)
        }

    @Test
    fun `it should reflect the correct user state when invoked with anonymous user callback`() =
        runTestUnconfined {
            // given
            val firebaseUser: FirebaseUser = mockk {
                every { isAnonymous } returns true
            }
            every { firebaseAuth.currentUser } returns firebaseUser
            createUserSessionManager()
            val expectedUserState = UserState.Anonymous

            // when
            authStateListener.onAuthStateChanged(firebaseAuth)

            // then
            assertThat(userSessionManager.userState.value).isEqualTo(expectedUserState)
        }

    @Test
    fun `it should reflect registered user without uid state correctly`() =
        runTestUnconfined {
            // given
            val firebaseUser: FirebaseUser = mockk {
                every { isAnonymous } returns false
            }
            every { firebaseAuth.currentUser } returns firebaseUser
            every { firebaseAuth.uid } returns null
            createUserSessionManager()
            val expectedUserState = UserState.Registered(hasConsented = false)

            // when
            authStateListener.onAuthStateChanged(firebaseAuth)

            // then
            assertThat(userSessionManager.userState.value).isEqualTo(expectedUserState)
        }

    @Test
    fun `it should reflect registered user consented state correctly`() =
        runTestUnconfined {
            // given
            val uid = "uid"
            val location = "users/$uid/signature.pdf"
            val firebaseUser: FirebaseUser = mockk {
                every { isAnonymous } returns false
            }
            val storageReference: StorageReference = mockk()
            val metadataTask: Task<StorageMetadata> = mockk {
                every { isComplete } returns true
                every { isCanceled } returns false
                every { exception } returns null
                every { result } returns mockk()
            }
            every { storageReference.metadata } returns metadataTask
            every { firebaseAuth.currentUser } returns firebaseUser
            every { firebaseAuth.uid } returns uid
            every { firebaseStorage.getReference(location) } returns storageReference
            createUserSessionManager()
            val expectedUserState = UserState.Registered(hasConsented = true)

            // when
            authStateListener.onAuthStateChanged(firebaseAuth)

            // then
            assertThat(userSessionManager.userState.value).isEqualTo(expectedUserState)
        }

    @Test
    fun `it should not upload consent pdf if current user is not available`() = runTestUnconfined {
        // given
        every { firebaseAuth.currentUser } returns null
        createUserSessionManager()

        // when
        val result = userSessionManager.uploadConsentPdf(byteArrayOf())

        // then
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `it should handle successful upload of consent pdf correctly`() = runTestUnconfined {
        // given
        setupPDFUpload(successful = true)
        createUserSessionManager()

        // when
        val result = userSessionManager.uploadConsentPdf(byteArrayOf())

        // then
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun `it should handle non successful upload consent pdf correctly`() = runTestUnconfined {
        // given
        setupPDFUpload(successful = false)
        createUserSessionManager()

        // when
        val result = userSessionManager.uploadConsentPdf(byteArrayOf())

        // then
        assertThat(result.isFailure).isTrue()
        assertThat(userSessionManager.userState.value).isEqualTo(UserState.NotInitialized)
    }

    private fun setupPDFUpload(successful: Boolean) {
        val uid = "uid"
        val location = "users/$uid/signature.pdf"
        val firebaseUser: FirebaseUser = mockk()
        every { firebaseUser.uid } returns uid
        every { firebaseAuth.currentUser } returns firebaseUser

        val taskResult: UploadTask.TaskSnapshot = mockk()
        val storageTask: StorageTask<UploadTask.TaskSnapshot> = mockk()
        every { storageTask.isSuccessful } returns successful
        val uploadTask: UploadTask = mockk {
            every { isComplete } returns true
            every { isCanceled } returns false
            every { exception } returns null
            every { result } returns taskResult
            every { result.task } returns storageTask
        }
        val storageReference: StorageReference = mockk()
        every { storageReference.putStream(any()) } returns uploadTask
        every { firebaseStorage.getReference(location) } returns storageReference
    }

    private fun createUserSessionManager() {
        val slot = slot<AuthStateListener>()
        every { firebaseAuth.addAuthStateListener(capture(slot)) } just Runs
        userSessionManager = UserSessionManager(
            firebaseStorage = firebaseStorage,
            firebaseAuth = firebaseAuth,
            ioDispatcher = UnconfinedTestDispatcher(),
            coroutineScope = SpeziTestScope(),
        )
        authStateListener = slot.captured
    }
}
