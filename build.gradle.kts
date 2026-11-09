plugins {
    kotlin("multiplatform") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
    id("com.vanniktech.maven.publish") version "0.37.0"
}

group = "io.github.godox"
version = "1.0.1"

repositories {
    mavenCentral()
}

tasks.withType<Test>().configureEach {
    testLogging { showStandardStreams = true }
}

kotlin {
    jvm()
    js(IR) { browser(); nodejs() }
    wasmJs { browser(); nodejs() }

    linuxX64()
    linuxArm64()
    mingwX64()
    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()
    watchosX64()
    watchosArm64()
    watchosSimulatorArm64()

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




mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(group.toString(), "deep-copy-serialize", version.toString())

    pom {
        name = "deep-copy-serialize"
        description = "A Kotlin Multiplatform library that deep-clones @Serializable values."
        inceptionYear = "2026"
        url = "https://github.com/Godox/deep-copy-serialize/"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = "Godox"
                name = "Godox"
                email = "12220474+Godox@users.noreply.github.com"
                organization = "Godox"
                organizationUrl = "https://github.com/Godox/"
                url = "https://github.com/Godox/"
            }
        }

        scm {
            url = "https://github.com/Godox/deep-copy-serialize/"
            connection = "scm:git:git://github.com/Godox/deep-copy-serialize.git"
            developerConnection = "scm:git:ssh://git@github.com/Godox/deep-copy-serialize.git"
        }
    }
}
