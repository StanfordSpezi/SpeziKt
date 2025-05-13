package edu.stanford.spezi.core

import android.content.Context

/**
 * An interface for providing a default module instance to be used in the module dependency graph in case no explicit instance is
 * registered in the configuration block of the [SpeziApplication]. In case the module is not registered, Spezi will try to create
 * the instance by checking whether the companion object of the Module type implements this interface.
 *
 * Example usage:
 *
 * ```kotlin
 * class MyModule(val packageName: String) : Module {
 *
 *     companion object : DefaultInitializer<MyModule> {
 *         override fun create(context: Context): MyModule {
 *             return MyModule(packageName = context.packageName)
 *         }
 *     }
 * }
 * ```
 */
interface DefaultInitializer<out M : Module> {
    fun create(context: Context): M
}
