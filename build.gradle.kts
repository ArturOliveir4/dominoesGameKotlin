plugins {
    kotlin("jvm") version "2.2.0"
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.14" // plugin JavaFX
}

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.8"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("MainKt")
}

dependencies {
    // Dependências JavaFX
    implementation("org.openjfx:javafx-controls:21.0.8")
    implementation("org.openjfx:javafx-fxml:21.0.8")

    testImplementation(kotlin("test"))
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
}

application {
    // Classe principal
    mainClass.set("MainKt")
}

kotlin {
    jvmToolchain(21)
}
