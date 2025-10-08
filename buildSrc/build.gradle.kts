import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // The Kotlin DSL plugin provides a convenient way to develop convention plugins.
    // Convention plugins are located in `src/main/kotlin`, with the file extension `.gradle.kts`,
    // and are applied in the project's `build.gradle.kts` files as required.
    `kotlin-dsl`
}
subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            // Establece el target JVM de Kotlin en 17 para todos los subproyectos
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}
kotlin {
    jvmToolchain(17)
}

dependencies {
    // Add a dependency on the Kotlin Gradle plugin, so that convention plugins can apply it.
    implementation(libs.kotlinGradlePlugin)
}
