plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "9.0.0-beta9"
}

repositories {
    mavenLocal()

    maven {
        url = uri("https://repo.extendedclip.com/releases/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://libraries.minecraft.net/")
    }

    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.md-5.net/content/groups/public/")
    }

    maven {
        url = uri("https://repo.fancyplugins.de/releases")
    }
}

dependencies {
    api(libs.org.mongodb.bson)
    api(libs.me.anjoismysign.anjo.anjo)
    api(libs.net.milkbowl.vault.vaultapi)
    api(libs.me.anjoismysign.skeramidcommands)
    api(libs.me.anjoismysign.holoworld)
    api(libs.me.anjoismysign.psa.api)
    api(libs.me.anjoismysign.winona)
    api(libs.me.anjoismysign.aesthetic)
    compileOnly("io.github.classgraph:classgraph:4.8.157")
    compileOnly(libs.org.apache.commons.commons.lang3)
    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.com.mojang.authlib)
    compileOnly(libs.com.fasterxml.jackson.core.jackson.databind)
    compileOnly(libs.com.fasterxml.jackson.dataformat.jackson.dataformat.yaml)
    compileOnly(libs.org.mongodb.mongodb.driver.sync)
    compileOnly(libs.com.sk89q.worldguard.worldguard.bukkit)
    compileOnly(libs.commons.io.commons.io)
    compileOnly(libs.com.github.decentsoftware.eu.decentholograms)
    compileOnly(libs.net.md.v5.bungeecord.api)
    compileOnly(libs.me.clip.placeholderapi)
    compileOnly(libs.libsdisguises.libsdisguises)
    compileOnly(libs.net.lingala.zip4j.zip4j)
    compileOnly(libs.de.oliver.fancyholograms)
}

group = "us.mytheria"
version = "1.698.31"
description = "bloblib"
java.sourceCompatibility = JavaVersion.VERSION_16
java.targetCompatibility = JavaVersion.VERSION_16

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    minimize()
    archiveClassifier.set("")
}
