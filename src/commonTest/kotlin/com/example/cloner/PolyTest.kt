package com.example.cloner

import kotlinx.serialization.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlin.test.assertEquals
import kotlin.test.Test

@Serializable
sealed interface Shape
@Serializable data class Circle(val r: Double) : Shape
@Serializable data class Rect(val w: Double, val h: Double) : Shape

@Serializable
data class Container(val shape: Shape)

class PolyTest {
    val module = SerializersModule {
        polymorphic(Shape::class) {
            subclass(Circle::class, Circle.serializer())
            subclass(Rect::class, Rect.serializer())
        }
    }

    @Test
    fun poly() {
        val original = Container(Rect(2.0, 3.0))
        val clone = Cloner.clone(Container.serializer(), original, module)
        assertEquals(original, clone)
        // also reified with module overload
        val clone2 = Cloner.clone(original, module)
        assertEquals(original, clone2)
    }
}
