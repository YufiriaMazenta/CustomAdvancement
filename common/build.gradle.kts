import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.text.SimpleDateFormat

plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev").version("1.5.5")
}

repositories {
    mavenLocal()
    maven("https://jitpack.io")
    mavenCentral()
}


dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("com.github.YufiriaMazenta:CrypticLib:1.0.1")
    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

tasks {
    jar {
        finalizedBy(reobfJar)
    }
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}

