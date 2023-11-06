import java.text.SimpleDateFormat

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow").version("7.1.2")
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
    compileOnly("commons-io:commons-io:2.14.0")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    implementation("com.github.YufiriaMazenta:CrypticLib:1.0.13")
    implementation(project(":common"))
    implementation(project(":v1_17_R1"))
    implementation(project(":v1_18_R1"))
    implementation(project(":v1_18_R2"))
    implementation(project(":v1_19_R1"))
    implementation(project(":v1_19_R2"))
    implementation(project(":v1_19_R3"))
    implementation(project(":v1_20_R1"))
    implementation(project(":v1_20_R2"))
}

group = "com.github.yufiriamazenta"
version = "1.0.0-dev28"
var pluginVersion: String = version.toString() + "-" + SimpleDateFormat("yyyyMMdd").format(System.currentTimeMillis())
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    val props = HashMap<String, String>()
    props["version"] = pluginVersion
    processResources {
        filesMatching("plugin.yml") {
            expand(props)
        }
        filesMatching("config.yml") {
            expand(props)
        }
    }
    assemble {
        dependsOn(shadowJar)
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    jar {
        destinationDirectory.set(layout.buildDirectory.dir("dev-libs"))
    }
    shadowJar {
        relocate("crypticlib", "com.github.yufiriamazenta.customadv.crypticlib")
        archiveFileName.set("CustomAdvancement-${version}.jar")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    publishing {
        publications.create<MavenPublication>("maven") {
            from(components["java"])
        }
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
        compileOnly("com.github.YufiriaMazenta:CrypticLib:1.0.13")
        compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
        compileOnly("com.google.code.gson:gson:2.10.1")
    }
    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }
    }
    java.sourceCompatibility = JavaVersion.VERSION_17
    java.targetCompatibility = JavaVersion.VERSION_17
}