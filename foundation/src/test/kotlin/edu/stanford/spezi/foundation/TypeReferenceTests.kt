package edu.stanford.spezi.foundation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TypeReferenceTests {

    @Test
    fun `it should handle same type equality correctly`() {
        // given
        val stringList1 = typeReference<List<String>>()
        val stringList2 = typeReference<List<String>>()

        // then
        assertThat(stringList1).isEqualTo(stringList2)
        assertThat(setOf(stringList1, stringList2)).hasSize(1)
    }

    @Test
    fun `it should handle different type building correctly`() {
        // given
        val stringList = typeReference<List<String>>()
        val intList = typeReference<List<Int>>()
        val stringSet = typeReference<Set<String>>()
        val intSet = typeReference<Set<Int>>()
        val customType = typeReference<SomeType>()
        val allTypes = setOf(stringList, intList, stringSet, intSet, customType)

        // then
        allTypes.forEach { current ->
            val otherTypes = allTypes.filterNot { it == current }
            otherTypes.forEach { assertThat(it).isNotEqualTo(current) }
        }
        assertThat(allTypes).hasSize(5)
    }

    private object SomeType
}
