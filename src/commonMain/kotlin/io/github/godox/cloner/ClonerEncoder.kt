package io.github.godox.cloner

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.*

/**
 * Custom format encoder that collects the serialized primitive components of a
 * value into an in-memory list. Used internally to clone serializable values.
 */
@OptIn(ExperimentalSerializationApi::class)
internal class ClonerEncoder(
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : AbstractEncoder() {

    val serializedValues: ArrayDeque<Any?> = ArrayDeque()

    override fun encodeValue(value: Any) {
        serializedValues.add(value)
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        encodeInt(collectionSize)
        return this
    }

    override fun encodeNull() {
        serializedValues.add(false)
    }

    override fun encodeNotNullMark() {
        serializedValues.add(true)
    }
}
