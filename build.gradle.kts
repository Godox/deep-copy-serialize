plugins {
    kotlin("multiplatform") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    // Add more targets here as needed, e.g. linuxX64(), iosArm64(), js(IR), wasmJs()
    // Note: Kotlin/Native compilation requires a supported host (Linux x86_64, macOS, Windows).
    // linuxArm64()

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.8.1")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        jvmMain.dependencies {
            // JVM-only dependencies can go here
        }
    }
}


