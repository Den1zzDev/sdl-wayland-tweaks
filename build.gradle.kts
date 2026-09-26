plugins {
    alias(libs.plugins.fabric.loom)
    id("maven-publish")
}

val mavenGroup = providers.gradleProperty("maven_group").get()
val modVersion = providers.gradleProperty("mod_version").get()

group = mavenGroup
version = "${libs.versions.minecraft.get()}-$modVersion"

base {
    archivesName.set(providers.gradleProperty("archives_base_name").get())
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get().toInt()))
    }
}

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven {
                name = "modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)

    val fapiVersion = libs.versions.fabric.api.get()
    implementation(fabricApi.module("fabric-api-base", fapiVersion))
    implementation(fabricApi.module("fabric-lifecycle-events-v1", fapiVersion))

    // Optional ModMenu integration
    compileOnly("maven.modrinth:modmenu:21.0.0-beta.1") {
        isTransitive = false
    }
}

tasks.processResources {
    val propertyMap = mapOf(
        "version" to project.version,
        "jdk_version" to libs.versions.jdk.get(),
        "minecraft_version" to "~26.3",
        "loader_version" to libs.versions.fabric.loader.get()
    )

    inputs.properties(propertyMap)
    filesMatching("fabric.mod.json") {
        expand(propertyMap)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(libs.versions.jdk.get().toInt())
}
