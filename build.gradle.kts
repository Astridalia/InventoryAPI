plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "github.astridalia"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.mineinabyss.com/releases")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    implementation("io.insert-koin:koin-core:3.5.0")
    implementation("org.reflections:reflections:0.10.2")
    compileOnly("org.spigotmc:spigot-api:1.14.1-R0.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}