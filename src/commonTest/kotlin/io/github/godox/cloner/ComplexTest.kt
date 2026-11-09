package io.github.godox.cloner

import kotlinx.serialization.Serializable
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.Test

@Serializable
data class Foo(
    val ch: Char,
    val ub: UByte,
    val ul: ULong,
    val map: Map<Int, String>,
    val sealed: Sealed,
    val nullable: String?,
    val list: List<Int>,
)

@Serializable
sealed interface Sealed
@Serializable data class A(val x: Int): Sealed
@Serializable data class B(val y: String): Sealed

class ComplexTest {
    @Test
    fun complex() {
        val original = Foo('z', 5u, 9UL, mapOf(1 to "a"), B("hi"), null, listOf(1, 2, 3))
        val clone = Cloner.clone(original)
        assertEquals(original, clone)
        assertTrue(original.list !== clone.list)
        assertTrue(original.map !== clone.map)
    }
}
