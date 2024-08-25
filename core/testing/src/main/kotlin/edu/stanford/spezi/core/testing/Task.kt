package edu.stanford.spezi.core.testing

import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk

/**
 * Returns a mockk task with the given result
 */
fun <T> mockTask(result: T): Task<T> = mockk {
    every { isComplete } returns true
    every { exception } returns null
    every { isCanceled } returns false
    every { this@mockk.result } returns result
}
