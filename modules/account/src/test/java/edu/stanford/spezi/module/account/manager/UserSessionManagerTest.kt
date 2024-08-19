package edu.stanford.spezi.module.account.manager

import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Before
import org.junit.Test
import java.util.UUID

class UserSessionManagerTest {
    private val firebaseStorage: FirebaseStorage = mockk()
    private val firebaseAuth: FirebaseAuth = mockk()

    private lateinit var userSessionManager: UserSessionManager

    @Before
    fun setup() {
        every { firebaseAuth.addAuthStateListener(any()) } just Runs
        every { firebaseAuth.removeAuthStateListener(any()) } just Runs
    }

    @Test
    fun `it should have not initialized user if currentUser is null`() = runTestUnconfined {
        // given
        every { firebaseAuth.currentUser } returns null
        createUserSessionManager()

        // when
        val state = userSessionManager.getUserState()

        // then
        assertThat(state).isEqualTo(UserState.NotInitialized)
    }

    @Test
    fun `it should reflect the correct anonymous user state`() = runTestUnconfined {
        // given
        val firebaseUser: FirebaseUser = mockk {
            every { isAnonymous } returns true
        }
        every { firebaseAuth.currentUser } returns firebaseUser
        createUserSessionManager()
        val expectedUserState = UserState.Anonymous

        // when
        val state = userSessionManager.getUserState()

        // then
        assertThat(state).isEqualTo(expectedUserState)
    }

    @Test
    fun `it should reflect registered user without uid state correctly`() = runTestUnconfined {
        // given
        val firebaseUser: FirebaseUser = mockk {
            every { isAnonymous } returns false
        }
        every { firebaseAuth.currentUser } returns firebaseUser
        every { firebaseAuth.uid } returns null
        createUserSessionManager()
        val expectedUserState = UserState.Registered(hasConsented = false)

        // when
        val state = userSessionManager.getUserState()

        // then
        assertThat(state).isEqualTo(expectedUserState)
    }

    @Test
    fun `it should reflect registered user consented state correctly`() = runTestUnconfined {
        // given
        setupConsented()
        createUserSessionManager()
        val expectedUserState = UserState.Registered(hasConsented = true)

        // when
        val state = userSessionManager.getUserState()

        // then
        assertThat(state).isEqualTo(expectedUserState)
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
    }

    @Test
    fun `it should emit NotInitialized when currentUser is null`() = runTestUnconfined {
        // given
        every { firebaseAuth.currentUser } returns null

        // when
        createUserSessionManager()

        // then
        assertObservedState(userState = UserState.NotInitialized, scope = this)
    }

    @Test
    fun `it should emit Anonymous when currentUser is anonymous`() = runTestUnconfined {
        // given
        val firebaseUser: FirebaseUser = mockk {
            every { isAnonymous } returns true
        }
        every { firebaseAuth.currentUser } returns firebaseUser

        // when
        createUserSessionManager()

        // then
        assertObservedState(userState = UserState.Anonymous, scope = this)
    }

    @Test
    fun `it should emit Registered with hasConsented false when uid is null`() = runTestUnconfined {
        // given
        val firebaseUser: FirebaseUser = mockk {
            every { isAnonymous } returns false
        }
        every { firebaseAuth.currentUser } returns firebaseUser
        every { firebaseAuth.uid } returns null

        // when
        createUserSessionManager()

        // then
        assertObservedState(userState = UserState.Registered(hasConsented = false), scope = this)
    }

    @Test
    fun `it should emit Registered with hasConsented true when user has consented`() =
        runTestUnconfined {
            // given
            setupConsented()

            // when
            createUserSessionManager()

            // then
            assertObservedState(userState = UserState.Registered(hasConsented = true), scope = this)
        }

    @Test
    fun `it should return the correct user uid`() {
        // given
        val uid = UUID.randomUUID().toString()
        every { firebaseAuth.uid } returns uid
        createUserSessionManager()

        // when
        val result = userSessionManager.getUserUid()

        // then
        assertThat(result).isEqualTo(uid)
    }

    @Test
    fun `it should return the correct user info`() {
        // given
        val eMail = "test@test.de"
        val name = "Test User"
        val firebaseUser: FirebaseUser = mockk {
            every { email } returns eMail
            every { displayName } returns name
        }
        every { firebaseAuth.currentUser } returns firebaseUser
        createUserSessionManager()

        // when
        val result = userSessionManager.getUserInfo()

        // then
        assertThat(result.email).isEqualTo(eMail)
        assertThat(result.name).isEqualTo(name)
    }

    private suspend fun assertObservedState(userState: UserState, scope: TestScope) {
        val slot = slot<FirebaseAuth.AuthStateListener>()
        var capturedState: UserState? = null
        every { firebaseAuth.addAuthStateListener(capture(slot)) } just Runs
        val job = scope.launch {
            capturedState = userSessionManager.observeUserState().first()
        }

        // when
        slot.captured.onAuthStateChanged(firebaseAuth)

        // then
        assertThat(capturedState).isEqualTo(userState)
        job.join()
    }

    private fun setupConsented() {
        val uid = "uid"
        val location = "users/$uid/consent/consent.pdf"
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
    }

    private fun setupPDFUpload(successful: Boolean) {
        val uid = "uid"
        val location = "users/$uid/consent/consent.pdf"
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
        userSessionManager = UserSessionManagerImpl(
            firebaseStorage = firebaseStorage,
            firebaseAuth = firebaseAuth,
            ioDispatcher = UnconfinedTestDispatcher(),
            coroutineScope = SpeziTestScope(),
        )
    }
}
