plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "com.example"
version = "1.0.0"

gradlePlugin {
    plugins {
        create("cloner") {
            id = "com.example.cloner"
            implementationClass = "com.example.cloner.gradle.ClonerPlugin"
        }
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}
