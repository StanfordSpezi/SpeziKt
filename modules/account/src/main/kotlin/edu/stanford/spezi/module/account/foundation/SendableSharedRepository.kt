package edu.stanford.spezi.module.account.foundation

import kotlin.reflect.KClass

interface SendableSharedRepository<Anchor> {
    operator fun <Value, Source : KnowledgeSource<Anchor, Value>> get(source: KClass<Source>): Value?
    operator fun <Value, Source : KnowledgeSource<Anchor, Value>> set(source: KClass<Source>, value: Value?)

    fun <Value : Any> collect(allOf: KClass<Value>): List<Value>
    fun <Value, Source : KnowledgeSource<Anchor, Value>> contains(source: KClass<Source>): Boolean
}
