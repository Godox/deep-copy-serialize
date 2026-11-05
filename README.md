# Serial Cloner

A Kotlin Multiplatform project that deep-clones `@Serializable` values.

The cloning logic lives in `commonMain` and works on every Kotlin target. It uses
a tiny custom Kotlin serialization format: a value is encoded into an in-memory
`ArrayDeque` of its primitive components and decoded straight back into a new
instance.

## Cloneable annotation

Instead of `@Serializable`, you can annotate a class with `@Cloneable` (a
`@MetaSerializable` annotation). The serialization plugin then generates a
serializer for it, and `Cloner.clone` works out of the box.

```kotlin
@Cloneable
data class Holder(val value: Int)
```

## How cloning works

1. **Guard** — `Cloner.clone` returns `null` immediately when the input is
   `null`.
2. **Encode** — `ClonerEncoder` (an `AbstractEncoder`) walks the object graph
   using the compiler-generated serializer. Every primitive value is appended to
   an in-memory `ArrayDeque<Any?>` (`serializedValues`). Nested objects and
   collections are encoded recursively into the same deque; collections store
   their size before their elements. Nullable values are marked with a `Boolean`
   marker (`true` for a present value, `false` for `null`), so a `String` such
   as `"NULL"` or `"!!"` can never be confused with a marker.
3. **Decode** — `ClonerDecoder` (an `AbstractDecoder`) reads that deque back.
   It pops values in the same order, reconstructs nested objects and
   collections, and returns a brand-new instance. `elementsCount` tracks how many
   elements belong to the current structure so `decodeElementIndex()` can signal
   `DECODE_DONE` at the right point.

Because every primitive, nested object, and collection is recreated during
decoding, the result is a true deep copy: the clone shares no mutable state
with the original.

Convenience extensions are also available:

```kotlin
val copy = original.clone()
val copy2 = original.clone(serializersModule)
```

## Usage

```kotlin
import com.example.cloner.Cloner
import kotlinx.serialization.Serializable

@Serializable
data class Person(val name: String, val age: Int, val tags: List<String> = emptyList())

fun main() {
    val original = Person("Ada", 36, listOf("dev", "math"))
    val copy = Cloner.clone(original)   // deep copy
}
```

Polymorphic values are supported by passing a `SerializersModule`:

```kotlin
val clone = Cloner.clone(value, serializersModule)
```

## Gradle plugin

To avoid configuring kotlinx-serialization manually, apply the provided plugin
in your module:

```kotlin
plugins {
    kotlin("multiplatform") // or kotlin("jvm")
    id("com.example.cloner") version "1.0.0"
}
```

The plugin applies `kotlin("plugin.serialization")` and adds the
`kotlinx-serialization-core` dependency. You still annotate your classes with
`@Serializable` (or a `@MetaSerializable` marker).

The dependency configuration is configurable:

```kotlin
cloner {
    dependencyConfiguration = "api" // default: "implementation"
    // set to "" to disable adding the dependency automatically
}
```

## Targets

`jvm()` is enabled by default. Uncomment additional targets in
`build.gradle.kts` (`linuxX64()`, `iosArm64()`, `js(IR)`, `wasmJs()`, …) to
target them. Kotlin/Native compilation requires a supported host platform.
