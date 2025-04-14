package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid.Module.Module
import edu.stanford.bdh.engagehf.application.dependency.DependencyRegistry
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty


// --- Required Dependency (non-nullable) ---
class SpeziDependency<T : Module> private constructor(
    private val id: String
) : ReadOnlyProperty<Any?, T> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return DependencyRegistry.getModuleCreateIfNotExists(id) as T
    }

    companion object {
        fun <T : Module> get(moduleId: String, clz: KClass<T>): SpeziDependency<T> {
            println("Creating required dependency for ${clz.simpleName}")
            DependencyRegistry.ensureFactoryExists(moduleId) {
                val module = clz.java.getDeclaredConstructor().newInstance()
                module.id = moduleId
                module
            }
            return SpeziDependency(moduleId)
        }
    }
}

// --- Optional Dependency (nullable) ---
class OptionalSpeziDependency<T : Module> private constructor(
    private val id: String
) : ReadOnlyProperty<Any?, T?> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return if (DependencyRegistry.hasModule(id)) {
            DependencyRegistry.getModuleCreateIfNotExists(id) as T
        } else {
            null
        }
    }

    companion object {
        fun <T : Module> get(moduleId: String, clz: KClass<T>): OptionalSpeziDependency<T> {
            println("Creating optional dependency for ${clz.simpleName}")
            return OptionalSpeziDependency(moduleId)
        }
    }
}

// --- Factory for required dependencies ---
public inline fun <reified T : Module> Dependency(moduleId: String? = null): SpeziDependency<T> {
    val resolvedId = moduleId ?: T::class.simpleName!!
    return SpeziDependency.get(resolvedId, T::class)
}

// --- Factory for optional dependencies ---
public inline fun <reified T : Module> OptionalDependency(moduleId: String? = null): OptionalSpeziDependency<T> {
    val resolvedId = moduleId ?: T::class.simpleName!!
    return OptionalSpeziDependency.get(resolvedId, T::class)
}
