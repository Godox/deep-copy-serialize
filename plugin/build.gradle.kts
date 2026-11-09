plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "io.github.godox"
version = "1.0.1"

gradlePlugin {
    plugins {
        create("cloner") {
            id = "io.github.godox.cloner"
            implementationClass = "io.github.godox.cloner.gradle.ClonerPlugin"
        }
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}
