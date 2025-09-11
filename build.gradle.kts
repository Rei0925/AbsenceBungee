plugins {
    kotlin("jvm") version "2.1.20-RC3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.github.rei0925"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://jitpack.io")
    maven("https://libraries.minecraft.net/")
    maven("https://oss.sonatype.org/content/repositories/snapshots" ) // This lets gradle find the BungeeCord files online
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.21-R0.3")
    implementation("net.kyori:adventure-platform-bungeecord:4.3.0")
    implementation ("co.aikar:acf-bungee:0.5.1-SNAPSHOT")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.2.0")
    implementation ("org.json:json:20231013")

}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("bungee.yml") {
        expand(props)
    }
}
