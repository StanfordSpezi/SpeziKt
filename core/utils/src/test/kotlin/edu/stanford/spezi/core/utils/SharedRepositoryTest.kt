package edu.stanford.spezi.core.utils

import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor
import edu.stanford.spezi.core.utils.foundation.SharedRepository
import edu.stanford.spezi.core.utils.foundation.builtin.ValueRepository
import edu.stanford.spezi.core.utils.foundation.knowledgesource.ComputedKnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.core.utils.foundation.knowledgesource.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.OptionalComputedKnowledgeSource
import org.junit.Test
import java.util.UUID

object TestAnchor : RepositoryAnchor

interface TestTypes {
    val value: Int
}

data class TestDataClass(override var value: Int) : TestTypes {
    companion object {
        val key = object : KnowledgeSource<TestAnchor, TestDataClass> {
            override val uuid = UUID()
        }
    }
}

@Suppress("detekt:UseDataClass")
class TestClass(override val value: Int) : TestTypes {
    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other as? TestClass)?.value == value
    }

    companion object {
        val key = object : KnowledgeSource<TestAnchor, TestClass> {
            override val uuid = UUID()
        }
    }
}

object KeyLike : KnowledgeSource<TestAnchor, TestClass> {
    override val uuid = UUID()
}

data class DefaultedTestDataClass(override val value: Int) : TestTypes {
    companion object {
        val key = object : DefaultProvidingKnowledgeSource<TestAnchor, DefaultedTestDataClass> {
            override val uuid = UUID()
            override val defaultValue get() = DefaultedTestDataClass(0)
        }
    }
}

data class ComputedTestDataClass(override val value: Int) : TestTypes {
    companion object {
        val alwaysComputeKey = object : ComputedKnowledgeSource<
            TestAnchor,
            ComputedTestDataClass,
            SharedRepository<TestAnchor>
            > {
            override val uuid = UUID()
            override val storagePolicy: ComputedKnowledgeSourceStoragePolicy
                = ComputedKnowledgeSourceStoragePolicy.AlwaysCompute
            override fun compute(repository: SharedRepository<TestAnchor>) =
                ComputedTestDataClass(computedValue)
        }

        val storeKey = object : ComputedKnowledgeSource<
            TestAnchor,
            ComputedTestDataClass,
            SharedRepository<TestAnchor>
            > {
            override val uuid = UUID()
            override val storagePolicy: ComputedKnowledgeSourceStoragePolicy
                = ComputedKnowledgeSourceStoragePolicy.Store
            override fun compute(repository: SharedRepository<TestAnchor>) =
                ComputedTestDataClass(computedValue)
        }
    }
}

data class OptionalComputedTestDataClass(override val value: Int) : TestTypes {
    companion object {
        const val TRUE = true

        val alwaysComputeKey = object : OptionalComputedKnowledgeSource<
            TestAnchor,
            OptionalComputedTestDataClass,
            SharedRepository<TestAnchor>
            > {
            override val uuid = UUID()
            override val storagePolicy: ComputedKnowledgeSourceStoragePolicy
                = ComputedKnowledgeSourceStoragePolicy.AlwaysCompute
            override fun compute(repository: SharedRepository<TestAnchor>): OptionalComputedTestDataClass? =
                optionalComputedValue?.let { OptionalComputedTestDataClass(it) }
        }

        val storeKey = object : OptionalComputedKnowledgeSource<
            TestAnchor,
            OptionalComputedTestDataClass,
            SharedRepository<TestAnchor>
            > {
            override val uuid = UUID()
            override val storagePolicy: ComputedKnowledgeSourceStoragePolicy
                = ComputedKnowledgeSourceStoragePolicy.Store
            override fun compute(repository: SharedRepository<TestAnchor>): OptionalComputedTestDataClass? =
                optionalComputedValue?.let { OptionalComputedTestDataClass(it) }
        }
    }
}

var computedValue: Int = 3
var optionalComputedValue: Int? = null

class SharedRepositoryTest {
    private val repository = ValueRepository<TestAnchor>()

    @Test
    fun testIteration() {
        repository[TestDataClass.key] = TestDataClass(3)

        for (value in repository) {
            assert(value.anySource === TestDataClass.key)
            assert(value.anyValue is TestDataClass)
            assert((value.anyValue as? TestDataClass) == TestDataClass(3))
        }
    }

    @Test
    fun testDefaultSubscript() {
        repository[TestDataClass.key, TestDataClass(35)].value = 23

        val value = repository[TestDataClass.key]
        assert(value?.value == 23)
    }

    @Test
    fun testSetAndGet() {
        val testDataClass = TestDataClass(42)
        repository[TestDataClass.key] = testDataClass
        val contentOfDataClass = repository[TestDataClass.key]
        assert(contentOfDataClass === testDataClass)

        val newTestDataClass = TestDataClass(24)
        repository[TestDataClass.key] = newTestDataClass
        val newContentOfDataClass = repository[TestDataClass.key]
        assert(newContentOfDataClass == newTestDataClass)

        repository[TestDataClass.key] = null
        val newerContentOfDataClass = repository[TestDataClass.key]
        assert(newerContentOfDataClass == null)
    }

    @Test
    fun testGetWithDefault() {
        val testDataClass = DefaultedTestDataClass(42)

        val defaultDataClass = repository[DefaultedTestDataClass.key]
        assert(defaultDataClass == DefaultedTestDataClass(0))

        // The cast is necessary, since it would otherwise not use the non-defaulted one
        val regularGet = repository[DefaultedTestDataClass.key as KnowledgeSource<TestAnchor, DefaultedTestDataClass>] ?: testDataClass
        assert(regularGet == testDataClass)
    }

    @Test
    fun testContains() {
        val testDataClass = TestDataClass(42)
        assert(!repository.contains(TestDataClass.key))

        repository[TestDataClass.key] = testDataClass
        assert(repository.contains(TestDataClass.key))

        repository[TestDataClass.key] = null
        assert(!repository.contains(TestDataClass.key))
    }

    @Test
    fun testGetAllThatConformTo() {
        val testDataClass = TestDataClass(42)
        repository[TestDataClass.key] = testDataClass
        val testClass = TestClass(42)
        repository[TestClass.key] = testClass

        val testTypes = repository.collect(allOf = TestTypes::class)
        assert(testTypes.count() == 2)
        assert(testTypes.all { it.value == 42 })
    }

    @Test
    fun testMutationDataClass() {
        val testDataClass = TestDataClass(42)
        repository[TestDataClass.key] = testDataClass

        val contentOfDataClass = repository[TestDataClass.key]
        contentOfDataClass?.value = 24
        // This is different than on iOS - here, data classes use reference semantics!
        assert(testDataClass.value == 24)
        assert(contentOfDataClass?.value == 24)
    }

    @Test
    fun testKeyLikeKnowledgeSource() {
        val testClass = TestClass(42)
        repository[KeyLike] = testClass

        val contentOfClass = repository[KeyLike]
        assert(contentOfClass == testClass)
    }

    @Test
    fun testComputedKnowledgeSourceAlwaysComputePolicy() {
        val value = repository[ComputedTestDataClass.alwaysComputeKey]
        val optionalValue = repository[OptionalComputedTestDataClass.alwaysComputeKey]

        assert(value.value == computedValue)
        assert(optionalValue?.value == optionalComputedValue)

        computedValue = 5
        optionalComputedValue = 4

        val newValue = repository[ComputedTestDataClass.alwaysComputeKey]
        val newOptionalValue = repository[OptionalComputedTestDataClass.alwaysComputeKey]
        assert(newValue.value == computedValue)
        assert(newOptionalValue?.value == optionalComputedValue)
    }

    @Test
    fun testComputedKnowledgeSourceStorePolicy() {
        val value = repository[ComputedTestDataClass.storeKey]
        val optionalValue = repository[OptionalComputedTestDataClass.storeKey]

        assert(value.value == computedValue)
        assert(optionalValue?.value == optionalComputedValue)

        // get call bypasses the compute call, so tests if it's really stored
        val getValue = repository[ComputedTestDataClass.alwaysComputeKey]
        val getOptionalValue = repository[OptionalComputedTestDataClass.alwaysComputeKey]

        assert(getValue.value == computedValue)
        assert(getOptionalValue?.value == optionalComputedValue) // this is nil

        // make sure computed knowledge sources with `Store` policy are not re-computed
        computedValue = 5
        optionalComputedValue = 4

        val newValue = repository[ComputedTestDataClass.alwaysComputeKey]
        val newOptionalValue = repository[OptionalComputedTestDataClass.alwaysComputeKey]

        assert(newValue.value == value.value)
        assert(newOptionalValue?.value == optionalComputedValue) // never stored as it was nil

        // last check if its really written now
        val writtenOptionalValue = repository[OptionalComputedTestDataClass.alwaysComputeKey]
        assert(writtenOptionalValue?.value == optionalComputedValue)

        // check again that it doesn't change
        optionalComputedValue = null
        assert(repository[OptionalComputedTestDataClass.storeKey]?.value == 4)
    }

    // TODO: fun testComputedKnowledgeSourcePreferred() has not been copied from iOS due to accessing the
    //  operator fun get will result in ambiguity compiler error, so this behavior would not be possible anyways

}
