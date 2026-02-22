package edu.stanford.spezi.foundation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

object TestAnchor : RepositoryAnchor

interface TestTypes {
    val value: Int
}

data class TestDataClass(override var value: Int) : TestTypes {
    data object Key : KnowledgeSource<TestAnchor, TestDataClass>
}

@Suppress("detekt:UseDataClass")
class TestClass(override val value: Int) : TestTypes {
    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other as? TestClass)?.value == value
    }

    data object Key : KnowledgeSource<TestAnchor, TestClass>
}

object KeyLike : KnowledgeSource<TestAnchor, TestClass>

data class DefaultedTestDataClass(override val value: Int) : TestTypes {
    object Key : DefaultProvidingKnowledgeSource<TestAnchor, DefaultedTestDataClass> {
        override val defaultValue get() = DefaultedTestDataClass(0)
    }
}

data class ComputedTestDataClass(override val value: Int) : TestTypes {
    object AlwaysComputeKey : ComputedKnowledgeSource<
        TestAnchor,
        ComputedTestDataClass,
        > {
        override val storagePolicy: ComputedKnowledgeSourceStoragePolicy =
            ComputedKnowledgeSourceStoragePolicy.AlwaysCompute

        override fun compute(repository: ValueRepository<TestAnchor>): ComputedTestDataClass {
            return ComputedTestDataClass(computedValue)
        }
    }

    object StoreKey : ComputedKnowledgeSource<TestAnchor, ComputedTestDataClass> {
        override val storagePolicy: ComputedKnowledgeSourceStoragePolicy =
            ComputedKnowledgeSourceStoragePolicy.Store

        override fun compute(repository: ValueRepository<TestAnchor>): ComputedTestDataClass {
            return ComputedTestDataClass(computedValue)
        }
    }
}

data class OptionalComputedTestDataClass(override val value: Int) : TestTypes {
    object AlwaysComputeKey : OptionalComputedKnowledgeSource<TestAnchor, OptionalComputedTestDataClass> {
        override val storagePolicy: ComputedKnowledgeSourceStoragePolicy =
            ComputedKnowledgeSourceStoragePolicy.AlwaysCompute

        override fun compute(repository: ValueRepository<TestAnchor>): OptionalComputedTestDataClass? =
            optionalComputedValue?.let { OptionalComputedTestDataClass(it) }
    }

    object StoreKey : OptionalComputedKnowledgeSource<TestAnchor, OptionalComputedTestDataClass> {
        override val storagePolicy: ComputedKnowledgeSourceStoragePolicy =
            ComputedKnowledgeSourceStoragePolicy.Store

        override fun compute(repository: ValueRepository<TestAnchor>): OptionalComputedTestDataClass? =
            optionalComputedValue?.let { OptionalComputedTestDataClass(it) }
    }
}

var computedValue: Int = 3
var optionalComputedValue: Int? = null

class ValueRepositoryTest {
    private val repository = ValueRepository<TestAnchor>()

    @Test
    fun testIteration() {
        repository[TestDataClass.Key::class] = TestDataClass(3)

        for (value in repository) {
            assertThat(value.key).isSameInstanceAs(TestDataClass.Key::class)
            assertThat(value.value is TestDataClass).isEqualTo(true)
            assertThat(value.value as? TestDataClass).isEqualTo(TestDataClass(3))
        }
    }

    @Test
    fun testDefaultSubscript() {
        // This test is mostly just kept in here to showcase how the access is different on Android
        // due to data classes having reference semantics.
        val initialValue = repository[TestDataClass.Key::class] ?: TestDataClass(35)
        initialValue.value = 23
        repository[TestDataClass.Key::class] = initialValue

        val value = repository[TestDataClass.Key::class]
        assertThat(value?.value).isEqualTo(23)
    }

    @Test
    fun testSetAndGet() {
        val testDataClass = TestDataClass(42)
        repository[TestDataClass.Key::class] = testDataClass
        val contentOfDataClass = repository[TestDataClass.Key::class]
        assertThat(contentOfDataClass).isEqualTo(testDataClass)

        val newTestDataClass = TestDataClass(24)
        repository[TestDataClass.Key::class] = newTestDataClass
        val newContentOfDataClass = repository[TestDataClass.Key::class]
        assertThat(newContentOfDataClass).isEqualTo(newTestDataClass)

        repository[TestDataClass.Key::class] = null
        val newerContentOfDataClass = repository[TestDataClass.Key::class]
        assertThat(newerContentOfDataClass).isNull()
    }

    @Test
    fun testGetWithDefault() {
        val defaultDataClass = repository[DefaultedTestDataClass.Key::class]
        assertThat(defaultDataClass).isEqualTo(DefaultedTestDataClass.Key.defaultValue)
    }

    @Test
    fun testContains() {
        val testDataClass = TestDataClass(42)
        assertThat(repository.contains(TestDataClass.Key::class)).isFalse()

        repository[TestDataClass.Key::class] = testDataClass
        assertThat(repository.contains(TestDataClass.Key::class)).isTrue()

        repository[TestDataClass.Key::class] = null
        assertThat(repository.contains(TestDataClass.Key::class)).isFalse()
    }

    @Test
    fun testGetAllThatConformTo() {
        val testDataClass = TestDataClass(42)
        repository[TestDataClass.Key::class] = testDataClass
        val testClass = TestClass(42)
        repository[TestClass.Key::class] = testClass

        val testTypes = repository.collect(allOf = TestTypes::class)
        assertThat(testTypes).hasSize(2)
        assertThat(testTypes.all { it.value == 42 }).isTrue()
    }

    @Test
    fun testMutationDataClass() {
        val testDataClass = TestDataClass(42)
        repository[TestDataClass.Key::class] = testDataClass

        val contentOfDataClass = repository[TestDataClass.Key::class]
        contentOfDataClass?.value = 24
        // This is different than on iOS - here, data classes use reference semantics!
        assertThat(testDataClass.value).isEqualTo(24)
        assertThat(contentOfDataClass?.value).isEqualTo(24)
    }

    @Test
    fun testKeyLikeKnowledgeSource() {
        val testClass = TestClass(42)
        repository[KeyLike::class] = testClass

        val contentOfClass = repository[KeyLike::class]
        assertThat(contentOfClass).isEqualTo(testClass)
    }

    @Test
    fun testComputedKnowledgeSourceAlwaysComputePolicy() {
        val value = repository[ComputedTestDataClass.AlwaysComputeKey::class]
        val optionalValue = repository[OptionalComputedTestDataClass.AlwaysComputeKey::class]

        assertThat(value.value).isEqualTo(computedValue)
        assertThat(optionalValue?.value).isEqualTo(optionalComputedValue)

        computedValue = 5
        optionalComputedValue = 4

        val newValue = repository[ComputedTestDataClass.AlwaysComputeKey::class]
        val newOptionalValue = repository[OptionalComputedTestDataClass.AlwaysComputeKey::class]
        assertThat(newValue.value).isEqualTo(computedValue)
        assertThat(newOptionalValue?.value).isEqualTo(optionalComputedValue)
    }

    @Test
    fun testComputedKnowledgeSourceStorePolicy() {
        val value = repository[ComputedTestDataClass.StoreKey::class]
        val optionalValue = repository[OptionalComputedTestDataClass.StoreKey::class]

        assertThat(value.value).isEqualTo(computedValue)
        assertThat(optionalValue?.value).isEqualTo(optionalComputedValue)

        // get call bypasses the compute call, so tests if it's really stored
        val getValue = repository[ComputedTestDataClass.AlwaysComputeKey::class]
        val getOptionalValue = repository[OptionalComputedTestDataClass.AlwaysComputeKey::class]

        assertThat(getValue.value).isEqualTo(computedValue)
        assertThat(getOptionalValue?.value).isEqualTo(optionalComputedValue) // this is nil

        // make sure computed knowledge sources with `Store` policy are not re-computed
        computedValue = 5
        optionalComputedValue = 4

        val newValue = repository[ComputedTestDataClass.AlwaysComputeKey::class]
        val newOptionalValue = repository[OptionalComputedTestDataClass.AlwaysComputeKey::class]

        assertThat(newValue.value).isEqualTo(value.value)
        assertThat(newOptionalValue?.value).isEqualTo(optionalComputedValue) // never stored as it was nil

        // last check if its really written now
        val writtenOptionalValue = repository[OptionalComputedTestDataClass.AlwaysComputeKey::class]
        assertThat(writtenOptionalValue?.value).isEqualTo(optionalComputedValue)

        // check again that it doesn't change
        optionalComputedValue = null
        assertThat(repository[OptionalComputedTestDataClass.StoreKey::class]?.value).isEqualTo(4)
    }

    // TODO: fun testComputedKnowledgeSourcePreferred() has not been copied from iOS due to accessing the
    //  operator fun get will result in ambiguity compiler error, so this behavior would not be possible anyways
}
