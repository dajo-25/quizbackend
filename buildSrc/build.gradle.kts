plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.squareup:kotlinpoet:1.14.2") // Useful for generating code, but I might just use string templates for simplicity if preferred. String templates are often easier for specific custom formats.
    // I'll stick to String templates for now to avoid learning curve overhead for this specific task, unless it gets complex.
    // Actually, simple string manipulation is fine for this scope.
}
