package edu.stanford.bdh.engagehf.application

import adamma.c4dhi.claid.Module.Module
import edu.stanford.bdh.engagehf.application.dependency.DependencyRegistry
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class SpeziDependency<T : Module> private constructor(
    private var id: String
) : ReadOnlyProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val moduleId = id
        return DependencyRegistry.getModuleCreateIfNotExists(moduleId) as T
    }


    companion object {
        operator fun <T : Module> get(moduleId: String, clz: KClass<T>): SpeziDependency<T> {
            println("Creating dependency for ${clz.simpleName}")

            DependencyRegistry.ensureFactoryExists(moduleId) {
                val module = clz.java.getDeclaredConstructor().newInstance()
                module.id = moduleId
                module
            }

            return SpeziDependency(moduleId)
        }
    }

}

public inline fun <reified T: Module> Dependency(moduleId: String? = null) : SpeziDependency<T> {
    val resolvedId = moduleId ?: T::class.simpleName!!

    return SpeziDependency.get<T>(resolvedId, T::class);
}