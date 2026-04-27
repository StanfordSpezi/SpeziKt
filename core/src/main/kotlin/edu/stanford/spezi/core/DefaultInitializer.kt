package edu.stanford.spezi.core

import android.content.Context

/**
 * An interface for providing a default instance of any type [T] to be used in the dependency
 * graph when no explicit registration is found in the configuration block of the [SpeziApplication].
 *
 * Spezi will check whether the companion object of the requested type implements this interface
 * and, if so, call [create] to produce an instance automatically.
 *
 * Example usage:
 *
 * ```kotlin
 * class MyService(val packageName: String) {
 *
 *     companion object : DefaultInitializer<MyService> {
 *         override fun create(context: Context): MyService {
 *             return MyService(packageName = context.packageName)
 *         }
 *     }
 * }
 * ```
 */
interface DefaultInitializer<out T : Any> {
    fun create(context: Context): T
}
