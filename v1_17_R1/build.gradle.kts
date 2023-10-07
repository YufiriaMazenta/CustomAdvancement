plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven("https://r.irepo.space/maven/")
    mavenCentral()
}


dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("com.github.YufiriaMazenta:CrypticLib:1.0.5")
    compileOnly("com.google.code.gson:gson:2.10.1")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.17.1")
    compileOnly(project(":common"))
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17