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
    compileOnly(project(":common"))
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

tasks {
    jar {
        finalizedBy(reobfJar)
    }
}

tasks.withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
}