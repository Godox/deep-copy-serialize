package io.github.godox.cloner

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MetaSerializable

/**
 * Marks a class as serializable for the purpose of cloning with [Cloner].
 *
 * It is a `@MetaSerializable` annotation, so the Kotlin serialization plugin
 * generates a serializer for any class annotated with it, just like
 * `@Serializable`.
 */
@OptIn(ExperimentalSerializationApi::class)
@MetaSerializable
@Target(AnnotationTarget.CLASS)
annotation class Cloneable
