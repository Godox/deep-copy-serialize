package com.example.cloner

import kotlinx.serialization.Serializable
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.Test

@Serializable
data class Box(val value: Int, val nested: Nested? = null)

@Serializable
data class Nested(val items: MutableList<Int>)

class ClonerTest {
    @Test
    fun clonesEqualButDistinct() {
        val original = Box(42, Nested(mutableListOf(1, 2, 3)))
        val clone = Cloner.clone(original)
        assertEquals(original, clone)
        assertTrue(original !== clone)
        assertTrue(original.nested !== clone.nested)
        assertTrue(original.nested!!.items !== clone.nested!!.items)
    }

    @Test
    fun cloneIsDeepCopy() {
        val original = Box(1, Nested(mutableListOf(10)))
        val clone = Cloner.clone(original)
        clone.nested!!.items.add(20)
        assertEquals(listOf(10), original.nested!!.items)
    }

    @Test
    fun cloneNullable() {
        val original: String? = null
        val clone = Cloner.clone(original)
        assertEquals(null, clone)
    }

    @Test
    fun cloneList() {
        val original = listOf(1, 2, 3)
        val clone = Cloner.clone(original)
        assertEquals(original, clone)
        assertTrue(original !== clone)
    }
}
