package com.example.cloner

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

@Cloneable
data class CloneableHolder(val value: Int, val nested: MutableList<Int>)

class CloneableAnnotationTest {
    @Test
    fun cloneableAnnotation() {
        val original = CloneableHolder(7, mutableListOf(1, 2))
        val copy = Cloner.clone(original)
        assertEquals(original, copy)
        assertTrue(original !== copy)
        assertTrue(original.nested !== copy.nested)
    }
}
