package com.example.cloner.gradle

/**
 * Configuration for the `com.example.cloner` Gradle plugin.
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
