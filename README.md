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

## Step-by-step example

The example below uses `Person(name, Address(city, zip), tags)` and shows how the
internal `ArrayDeque` is filled during encoding and emptied during decoding.

```kotlin
val original = Person("Ada", Address("Paris", 75000), mutableListOf("dev", "math"))

// ---------- ENCODING (fills the deque) ----------
// ClonerEncoder appends primitive components in traversal order:
//   "Ada"  -> ["Ada"]
//   "Paris" -> ["Ada", "Paris"]
//   75000  -> ["Ada", "Paris", 75000]
//   tags size 2 -> ["Ada", "Paris", 75000, 2]
//   "dev"  -> ["Ada", "Paris", 75000, 2, "dev"]
//   "math" -> ["Ada", "Paris", 75000, 2, "dev", "math"]
//
// Final encoded deque:
// ["Ada", "Paris", 75000, 2, "dev", "math"]

val copy = Cloner.clone(original)

// ---------- DECODING (empties the deque) ----------
// ClonerDecoder pops values in the same order:
//   pop "Ada"   -> deque ["Paris", 75000, 2, "dev", "math"]
//   pop "Paris" -> deque [75000, 2, "dev", "math"]
//   pop 75000   -> deque [2, "dev", "math"]
//   pop 2       -> collection size, deque ["dev", "math"]
//   pop "dev"   -> deque ["math"]
//   pop "math"  -> deque []
//
// Result: Person("Ada", Address("Paris", 75000), ["dev", "math"])

println(original === copy)            // false
println(original.address === copy.address) // false
println(original.tags === copy.tags)       // false
```

## Usage

```kotlin
import io.github.godox.cloner.Cloner
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
    id("io.github.godox.cloner") version "1.0.0"
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

## Benchmarks

Measured on the JVM (single thread) by cloning a `List<BenchItem>` of the given
size 10 times and averaging. `BenchItem` holds an `Int`, a `String` and a
`List<String>` of 5 elements.

| Instances cloned | Cloner.clone (ms) | Data class copy (ms) |
|-----------------:|------------------:|---------------------:|
| 10               | 0.2               | 0.0                  |
| 100              | 0.8               | 0.1                  |
| 1000             | 1.4               | 0.3                  |
| 10000            | 6.0               | 3.1                  |

The "Data class copy" column measures a hand-written deep copy using
`copy()` plus `toMutableList()` on the nested list. The benchmark lives in
`commonTest` (`BenchmarkTest`) and can be re-run with
`./gradlew jvmTest --tests "io.github.godox.cloner.BenchmarkTest"`.

## Targets

`jvm()` is enabled by default. Uncomment additional targets in
`build.gradle.kts` (`linuxX64()`, `iosArm64()`, `js(IR)`, `wasmJs()`, …) to
target them. Kotlin/Native compilation requires a supported host platform.
