package io.github.godox.cloner

import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.time.measureTime

@Serializable
data class BenchItem(val id: Int, val name: String, val tags: List<String>)

class BenchmarkTest {
    @Test
    fun benchmarkClone() {
        println("BENCHMARK_START")
        for (n in listOf(10, 100, 1000, 10000)) {
            val items = List(n) { BenchItem(it, "name$it", List(5) { "t$it" }) }
            // warmup
            repeat(3) { Cloner.clone(items) }
            repeat(3) { items.map { it.copy(tags = it.tags.toMutableList()) } }

            val cloneTotal = measureTime { repeat(10) { Cloner.clone(items) } }
            val copyTotal = measureTime { repeat(10) { items.map { it.copy(tags = it.tags.toMutableList()) } } }

            val cloneMs = cloneTotal.inWholeMilliseconds / 10.0
            val copyMs = copyTotal.inWholeMilliseconds / 10.0
            println("BENCH n=$n cloneMs=$cloneMs copyMs=$copyMs")
        }
        println("BENCHMARK_END")
    }
}
