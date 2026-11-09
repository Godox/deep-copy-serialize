package io.github.godox.cloner.gradle

/**
 * Configuration for the `io.github.godox.cloner` Gradle plugin.
 */
open class ClonerExtension {
    /**
     * Gradle configuration to add `kotlinx-serialization-core` to.
     * Set to an empty string to disable adding the dependency automatically.
     */
    var dependencyConfiguration: String = "implementation"

    /**
     * Version of `kotlinx-serialization-core` to add.
     */
    var kotlinxSerializationVersion: String = "1.8.1"
}
