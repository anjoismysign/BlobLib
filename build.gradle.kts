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
    api(libs.io.github.anjoismysign.anjo)
    api(libs.net.milkbowl.vault.vaultapi)
    api(libs.io.github.anjoismysign.skeramidcommands)
    api(libs.io.github.anjoismysign.holoworld)
    api(libs.io.github.anjoismysign.psa.api)
    api(libs.io.github.anjoismysign.psa.sql)
    api(libs.io.github.anjoismysign.winona)
    api(libs.io.github.anjoismysign.aesthetic)
    compileOnly("io.github.classgraph:classgraph:4.8.157")
    compileOnly(libs.org.apache.commons.commons.lang3)
    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.com.mojang.authlib)
    compileOnly(libs.org.mongodb.mongodb.driver.sync)
    compileOnly(libs.com.sk89q.worldguard.worldguard.bukkit)
    compileOnly(libs.commons.io.commons.io)
    compileOnly(libs.com.github.decentsoftware.eu.decentholograms)
    compileOnly(libs.net.md.v5.bungeecord.api)
    compileOnly(libs.me.clip.placeholderapi)
    compileOnly(libs.libsdisguises.libsdisguises)
    compileOnly(libs.net.lingala.zip4j.zip4j)
    compileOnly(libs.de.oliver.fancyholograms)
//    mvn install:install-file \
//    -Dfile=weaponmechanics-4.1.5.jar \
//    -DpomFile=weaponmechanics-4.1.5.pom.xml
    compileOnly("com.cjcrafter:weaponmechanics:4.1.5")
//    mvn install:install-file \
//    -Dfile=mechanicscore-4.1.5.jar \
//    -DpomFile=mechanicscore-4.1.5.pom.xml
    compileOnly("com.cjcrafter:mechanicscore:4.1.5")
}

group = "io.github.anjoismysign"
version = "1.698.32"
description = "bloblib"
java.sourceCompatibility = JavaVersion.VERSION_16
java.targetCompatibility = JavaVersion.VERSION_16

publishing {
    publications.create<MavenPublication>("maven") {
        artifact(tasks.shadowJar) {
            classifier = null
        }

        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    minimize()
    archiveClassifier.set("")
    archiveFileName.set("${project.name}-${project.version}.jar")
}
