package io.github.godox.cloner

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.*

/**
 * Custom format decoder that rebuilds a value from the in-memory list produced
 * by [ClonerEncoder]. Used internally to clone serializable values.
 */
@OptIn(ExperimentalSerializationApi::class)
internal class ClonerDecoder(
    val values: ArrayDeque<Any?>,
    var elementsCount: Int = 0,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : AbstractDecoder() {

    private var elementIndex = 0

    override fun decodeValue(): Any = values.removeFirst()!!

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (elementIndex == elementsCount) return CompositeDecoder.DECODE_DONE
        return elementIndex++
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
        ClonerDecoder(values, descriptor.elementsCount, serializersModule)

    override fun decodeSequentially(): Boolean = true

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int =
        decodeInt().also { elementsCount = it }

    override fun decodeNotNullMark(): Boolean {
        // The marker is a Boolean: true means "a value follows", false means null.
        return decodeValue() as Boolean
    }
}
