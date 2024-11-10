package edu.stanford.spezi.core.utils

import com.google.common.truth.Truth.assertThat
import edu.stanford.spezi.core.utils.foundation.Repository
import edu.stanford.spezi.core.utils.foundation.RepositoryAnchor
import edu.stanford.spezi.core.utils.foundation.builtin.ValueRepository
import edu.stanford.spezi.core.utils.foundation.knowledgesource.ComputedKnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.ComputedKnowledgeSourceStoragePolicy
import edu.stanford.spezi.core.utils.foundation.knowledgesource.DefaultProvidingKnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.KnowledgeSource
import edu.stanford.spezi.core.utils.foundation.knowledgesource.OptionalComputedKnowledgeSource
import org.junit.Test

object TestAnchor : RepositoryAnchor

interface TestTypes {
    val value: Int
}

data class TestDataClass(override var value: Int) : TestTypes {
    companion object {
        val key = object : KnowledgeSource<TestAnchor, TestDataClass> {}
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
        val key = object : KnowledgeSource<TestAnchor, TestClass> {}
    }
}

object KeyLike : KnowledgeSource<TestAnchor, TestClass>

data class DefaultedTestDataClass(override val value: Int) : TestTypes {
    companion object {
        val key = object : DefaultProvidingKnowledgeSource<TestAnchor, DefaultedTestDataClass> {
            override val defaultValue get() = DefaultedTestDataClass(0)
        }
    }
}

data class ComputedTestDataClass(override val value: Int) : TestTypes {
    companion object {
        val alwaysComputeKey = object : ComputedKnowledgeSource<
            TestAnchor,
            ComputedTestDataClass,
            > {
            override val storagePolicy: ComputedKnowledgeSourceStoragePolicy =
                ComputedKnowledgeSourceStoragePolicy.AlwaysCompute
            override fun compute(repository: Repository<TestAnchor>) =
                ComputedTestDataClass(computedValue)
        }

        val storeKey = object : ComputedKnowledgeSource<TestAnchor, ComputedTestDataClass> {
            override val storagePolicy: ComputedKnowledgeSourceStoragePolicy =
                ComputedKnowledgeSourceStoragePolicy.Store
            override fun compute(repository: Repository<TestAnchor>) =
                ComputedTestDataClass(computedValue)
        }
    }
}

data class OptionalComputedTestDataClass(override val value: Int) : TestTypes {
    companion object {
        val alwaysComputeKey = object : OptionalComputedKnowledgeSource<TestAnchor, OptionalComputedTestDataClass> {
            override val storagePolicy: ComputedKnowledgeSourceStoragePolicy =
                ComputedKnowledgeSourceStoragePolicy.AlwaysCompute
            override fun compute(repository: Repository<TestAnchor>): OptionalComputedTestDataClass? =
                optionalComputedValue?.let { OptionalComputedTestDataClass(it) }
        }

        val storeKey = object : OptionalComputedKnowledgeSource<TestAnchor, OptionalComputedTestDataClass> {
            override val storagePolicy: ComputedKnowledgeSourceStoragePolicy =
                ComputedKnowledgeSourceStoragePolicy.Store
            override fun compute(repository: Repository<TestAnchor>): OptionalComputedTestDataClass? =
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
            assertThat(value.key).isSameInstanceAs(TestDataClass.key)
            assertThat(value.value is TestDataClass).isEqualTo(true)
            assertThat(value.value as? TestDataClass).isEqualTo(TestDataClass(3))
        }
    }

    @Test
    fun testDefaultSubscript() {
        repository[TestDataClass.key, TestDataClass(35)].value = 23

        val value = repository[TestDataClass.key]
        assertThat(value?.value).isEqualTo(23)
    }

    @Test
    fun testSetAndGet() {
        val testDataClass = TestDataClass(42)
        repository[TestDataClass.key] = testDataClass
        val contentOfDataClass = repository[TestDataClass.key]
        assertThat(contentOfDataClass).isEqualTo(testDataClass)

        val newTestDataClass = TestDataClass(24)
        repository[TestDataClass.key] = newTestDataClass
        val newContentOfDataClass = repository[TestDataClass.key]
        assertThat(newContentOfDataClass).isEqualTo(newTestDataClass)

        repository[TestDataClass.key] = null
        val newerContentOfDataClass = repository[TestDataClass.key]
        assertThat(newerContentOfDataClass).isNull()
    }

    @Test
    fun testGetWithDefault() {
        val testDataClass = DefaultedTestDataClass(42)

        val defaultDataClass = repository[DefaultedTestDataClass.key]
        assertThat(defaultDataClass).isEqualTo(DefaultedTestDataClass(0))

        // The cast is necessary, since it would otherwise not use the non-defaulted one
        val regularGet = repository[DefaultedTestDataClass.key as KnowledgeSource<TestAnchor, DefaultedTestDataClass>] ?: testDataClass
        assertThat(regularGet).isEqualTo(testDataClass)
    }

    @Test
    fun testContains() {
        val testDataClass = TestDataClass(42)
        assertThat(repository.contains(TestDataClass.key)).isFalse()

        repository[TestDataClass.key] = testDataClass
        assertThat(repository.contains(TestDataClass.key)).isTrue()

        repository[TestDataClass.key] = null
        assertThat(repository.contains(TestDataClass.key)).isFalse()
    }

    @Test
    fun testGetAllThatConformTo() {
        val testDataClass = TestDataClass(42)
        repository[TestDataClass.key] = testDataClass
        val testClass = TestClass(42)
        repository[TestClass.key] = testClass

        val testTypes = repository.collect(allOf = TestTypes::class)
        assertThat(testTypes).hasSize(2)
        assertThat(testTypes.all { it.value == 42 }).isTrue()
    }

    @Test
    fun testMutationDataClass() {
        val testDataClass = TestDataClass(42)
        repository[TestDataClass.key] = testDataClass

        val contentOfDataClass = repository[TestDataClass.key]
        contentOfDataClass?.value = 24
        // This is different than on iOS - here, data classes use reference semantics!
        assertThat(testDataClass.value).isEqualTo(24)
        assertThat(contentOfDataClass?.value).isEqualTo(24)
    }

    @Test
    fun testKeyLikeKnowledgeSource() {
        val testClass = TestClass(42)
        repository[KeyLike] = testClass

        val contentOfClass = repository[KeyLike]
        assertThat(contentOfClass).isEqualTo(testClass)
    }

    @Test
    fun testComputedKnowledgeSourceAlwaysComputePolicy() {
        val value = repository[ComputedTestDataClass.alwaysComputeKey]
        val optionalValue = repository[OptionalComputedTestDataClass.alwaysComputeKey]

        assertThat(value.value).isEqualTo(computedValue)
        assertThat(optionalValue?.value).isEqualTo(optionalComputedValue)

        computedValue = 5
        optionalComputedValue = 4

        val newValue = repository[ComputedTestDataClass.alwaysComputeKey]
        val newOptionalValue = repository[OptionalComputedTestDataClass.alwaysComputeKey]
        assertThat(newValue.value).isEqualTo(computedValue)
        assertThat(newOptionalValue?.value).isEqualTo(optionalComputedValue)
    }

    @Test
    fun testComputedKnowledgeSourceStorePolicy() {
        val value = repository[ComputedTestDataClass.storeKey]
        val optionalValue = repository[OptionalComputedTestDataClass.storeKey]

        assertThat(value.value).isEqualTo(computedValue)
        assertThat(optionalValue?.value).isEqualTo(optionalComputedValue)

        // get call bypasses the compute call, so tests if it's really stored
        val getValue = repository[ComputedTestDataClass.alwaysComputeKey]
        val getOptionalValue = repository[OptionalComputedTestDataClass.alwaysComputeKey]

        assertThat(getValue.value).isEqualTo(computedValue)
        assertThat(getOptionalValue?.value).isEqualTo(optionalComputedValue) // this is nil

        // make sure computed knowledge sources with `Store` policy are not re-computed
        computedValue = 5
        optionalComputedValue = 4

        val newValue = repository[ComputedTestDataClass.alwaysComputeKey]
        val newOptionalValue = repository[OptionalComputedTestDataClass.alwaysComputeKey]

        assertThat(newValue.value).isEqualTo(value.value)
        assertThat(newOptionalValue?.value).isEqualTo(optionalComputedValue) // never stored as it was nil

        // last check if its really written now
        val writtenOptionalValue = repository[OptionalComputedTestDataClass.alwaysComputeKey]
        assertThat(writtenOptionalValue?.value).isEqualTo(optionalComputedValue)

        // check again that it doesn't change
        optionalComputedValue = null
        assertThat(repository[OptionalComputedTestDataClass.storeKey]?.value).isEqualTo(4)
    }

    // TODO: fun testComputedKnowledgeSourcePreferred() has not been copied from iOS due to accessing the
    //  operator fun get will result in ambiguity compiler error, so this behavior would not be possible anyways
}
