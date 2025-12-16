import org.gradle.internal.impldep.com.google.common.collect.ImmutableList

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.essential.gg/repository/maven-public")
        maven("https://maven.architectury.dev")
        maven("https://maven.fabricmc.net")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
    }
    // We also recommend specifying your desired version here if you're using more than one of the plugins,
    // so you do not have to change the version in multilpe places when updating.
    plugins {
        val egtVersion = "0.6.10" // should be whatever is displayed in above badge
        id("gg.essential.multi-version.root") version egtVersion
        id("gg.essential.multi-version.api-validation") version egtVersion
    }
}

fun Int.formatVersionNumber(): String {
    val str = this.toString()
    val part2 = str.substring(1, 3)
    val part3 = str.substring(3, 5).trimStart('0')
    return "${str[0]}.$part2${if (part3.isNotEmpty()) ".$part3" else ""}"
}

fun MutableList<String>.version(mcVersion: Int, forge: Boolean = true, neoforge: Boolean = true): MutableList<String> {
    val verString = mcVersion.formatVersionNumber()

    this.add("$verString-fabric")
    if (forge) this.add("$verString-forge")
    if (neoforge) this.add("$verString-neoforge")

    return this
}

mutableListOf<String>()
    .version(12111, neoforge = false)
    .version(12109)
    .version(12106)
    .version(12105)
    .version(12104)
    .version(12103)
    .version(12100)
    .version(12006)
    .version(12004)
    .version(12002)
    .version(12001, neoforge = false)
.forEach { version ->
    include(":$version")
    project(":$version").apply {
        // This is where the `build` folder and per-version overwrites will reside
        projectDir = file("versions/$version")
        // All sub-projects get configured by the same `build.gradle.kts` file, the string is relative to projectDir
        // You could use separate build files for each project, but usually that would just be duplicating lots of code
        buildFileName = "../../build.gradle.kts"
    }
}

// We use the `build.gradle.kts` file for all the sub-projects (cause that's where most the interesting stuff lives),
// so we need to use a different build file for the original root project.
rootProject.buildFileName = "root.gradle.kts"