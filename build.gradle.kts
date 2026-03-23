plugins {
    kotlin("jvm") version "2.2.0"
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0" // plugin JavaFX
}

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.8"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    mainModule.set("dominoes.game.kotlin")
    mainClass.set("app.MainKt")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.13")
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaExec>().configureEach {
    modularity.inferModulePath.set(true)
}
