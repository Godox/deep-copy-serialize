package io.github.godox.cloner

import kotlinx.serialization.*
import kotlinx.serialization.modules.*

/**
 * Deep-clones serializable values.
 *
 * The clone is produced by encoding the value into an in-memory list of its
 * primitive components using a custom Kotlin serialization format and decoding
 * that list straight back. Because the whole round trip happens in memory, no
 * JSON (and no temporary file) is ever written.
 */
@OptIn(ExperimentalSerializationApi::class)
object Cloner {

    /**
     * Deep-clones [value] using its [serializer].
     *
     * @param serializer serializer for [T]
     * @param value value to clone
     * @param serializersModule module used to resolve polymorphic/contextual serializers
     * @return a deep copy of [value]
     * @see Cloner
     */
    fun <T> clone(
        serializer: KSerializer<T>,
        value: T,
        serializersModule: SerializersModule = EmptySerializersModule(),
    ): T {
        if (value == null) return null as T
        val encoder = ClonerEncoder(serializersModule)
        encoder.encodeSerializableValue(serializer, value)

        val decoder = ClonerDecoder(encoder.serializedValues, serializersModule = serializersModule)
        return decoder.decodeSerializableValue(serializer)
    }

    /**
     * Deep-clones [value].
     *
     * @param value value to clone
     * @return a deep copy of [value]
     * @see Cloner
     */
    inline fun <reified T> clone(value: T): T {
        val serializer = serializer<T>()
        return clone(serializer, value)
    }

    /**
     * Deep-clones [value] using [serializersModule] for polymorphic/contextual types.
     *
     * @param value value to clone
     * @param serializersModule module used to resolve polymorphic/contextual serializers
     * @return a deep copy of [value]
     * @see Cloner
     */
    inline fun <reified T> clone(
        value: T,
        serializersModule: SerializersModule,
    ): T {
        val serializer = serializer<T>()
        return clone(serializer, value, serializersModule)
    }
}

/**
 * Convenience extension that clones the receiver using [Cloner].
 */
/**
 * Deep-clones this value.
 *
 * @see Cloner
 */
inline fun <reified T : Any> T.clone(): T = Cloner.clone(this)

/**
 * Deep-clones this value using [serializersModule] for polymorphic/contextual types.
 *
 * @see Cloner
 */
inline fun <reified T : Any> T.clone(serializersModule: SerializersModule): T =
    Cloner.clone(this, serializersModule)
