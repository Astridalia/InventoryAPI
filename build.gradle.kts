import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("maven-publish")
}

val pubComponent = components.find { it.name == "java" || it.name == "release" }

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.astridalia"
            artifactId = "InventoryAPI"
            version = "1.0.0"
            from(components["java"])
        }
    }
}

group = "com.github.astridalia"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.aikar.co/content/groups/aikar/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/groups/public/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://repo.mineinabyss.com/releases") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
}

dependencies {
    implementation("io.insert-koin:koin-core:3.5.0")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.spigotmc:spigot-api:1.14.1-R0.1-SNAPSHOT")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

tasks.register<ShadowJar>("buildArtifacts") {
    group = "build"
    description = "Build artifacts using the Shadow plugin"

    from(project.sourceSets.getByName("main").output)
    configurations = listOf(project.configurations.getByName("runtimeClasspath"))

    archiveClassifier.set("shaded") // You can customize the classifier as needed
}
