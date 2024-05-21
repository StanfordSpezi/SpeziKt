package edu.stanford.spezi.core.testing

import io.mockk.MockKVerificationScope
import io.mockk.coVerify
import io.mockk.verify

/**
 * Verifies that a specific interaction with a mock object never occurred.
 *
 * This function is a wrapper around the `verify` function from MockK that ensures
 * the given `verifyBlock` was executed exactly zero times.
 *
 * Usage:
 * ```kotlin
 * verifyNever { mock.someFunction() }
 * ```
 *
 * @param verifyBlock The block of code containing the interaction to verify.
 */
fun verifyNever(verifyBlock: MockKVerificationScope.() -> Unit) = verify(exactly = 0, verifyBlock = verifyBlock)


/**
 * Verifies that a specific interaction with a mock object in a coroutine context never occurred.
 *
 * This function is a wrapper around the `coVerify` function from MockK that ensures
 * the given `verifyBlock` was executed exactly zero times within a coroutine.
 *
 * Usage:
 * ```kotlin
 * coVerifyNever { mock.someSuspendFunction() }
 * ```
 *
 * @param verifyBlock The suspendable block of code containing the interaction to verify.
 */
fun coVerifyNever(verifyBlock: suspend MockKVerificationScope.() -> Unit) = coVerify(exactly = 0, verifyBlock = verifyBlock)
