package com.example.cloner.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Applies the Kotlin serialization plugin and adds the core serialization
 * dependency so consumers don't have to configure them manually.
 *
 * The dependency configuration is configurable through the `cloner` extension:
 *
 * ```kotlin
 * cloner {
 *     dependencyConfiguration = "api" // or "implementation", "compileOnly", ...
 * }
 * ```
 */
class ClonerPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.plugin.serialization")

        val extension = target.extensions.create("cloner", ClonerExtension::class.java)

        target.afterEvaluate {
            val configuration = extension.dependencyConfiguration.trim()
            if (configuration.isNotEmpty()) {
                target.dependencies.add(
                    configuration,
                    "org.jetbrains.kotlinx:kotlinx-serialization-core:${extension.kotlinxSerializationVersion}",
                )
            }
        }
    }
}
