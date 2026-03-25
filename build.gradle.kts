
//import gg.essential.gradle.multiversion.StripReferencesTransform.Companion.registerStripReferencesAttribute
import gg.essential.gradle.util.*
//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // If you're using Kotlin, it needs to be applied before the multi-version plugin
    //kotlin("jvm")

    // Apply the multi-version plugin, this does all the configuration necessary for the preprocessor to
    // work. In particular it also applies `com.replaymod.preprocess`.
    // In addition it primarily also provides a `platform` extension which you can use in this build script
    // to get the version and mod loader of the current project.
    id("gg.essential.multi-version")
    // If you do not care too much about the details, you can just apply essential-gradle-toolkits' defaults for
    // Minecraft, fabric-loader, forge, mappings, etc. versions.
    // You can also overwrite some of these if need be. See the `gg.essential.defaults.loom` README section.
    // Otherwise you'll need to configure those as usual for (architectury) loom.
    id("gg.essential.defaults")
}

//tasks.compileKotlin.setJvmDefault("all")
//tasks.withType<KotlinCompile> {
//    kotlinOptions {
//        languageVersion = "1.9"
//        apiVersion = "1.9"
//    }
//}

repositories {
    mavenCentral()
    maven("https://api.modrinth.com/maven")
    maven("https://mvnrepository.com/artifact/com.demonwav.mcdev/annotations")
    maven("https://maven.terraformersmc.com/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

val mcVersion = platform.mcVersion
val modVersion = properties["mod_version"].toString()

base.archivesName.set("entity_texture_features-$modVersion-${project.name}")

val manuallyAccessTransform = mcVersion >= 26_00_00 && platform.isNeoForge
val accessWidener = "entity_texture_features_" + when {
    mcVersion >= 26_01_00 -> 14
    mcVersion >= 1_21_11 -> 13
    mcVersion >= 1_21_09 -> 12
    mcVersion >= 1_21_06 -> 11
    mcVersion >= 1_21_05 -> 10
    mcVersion >= 1_21_04 -> 9
    mcVersion >= 1_21_02 -> 8
    mcVersion >= 1_21_00 -> 7
    mcVersion >= 1_20_06 -> 6
    mcVersion >= 1_20_04 -> 5
    mcVersion >= 1_20_02 -> 4
    mcVersion >= 1_20_01 -> 3
    else -> throw IllegalStateException("Unsupported version: $mcVersion")
} + ".accesswidener"

//val common = registerStripReferencesAttribute("common") {
//    excludes.add("net.minecraft")
//}

dependencies {
    // If you are depending on a multi-version library following the same scheme as the Essential libraries (that is
    // e.g. `elementa-1.8.9-forge`), you can `toString` `platform` directly to get the respective artifact id.
//    api("gg.essential:elementa:710")


    // universalcraft neoforge is just forge currently
//    val ucVer =
//        if(platform.isNeoForge) platform.toString().replace("neo","")
//        else platform.toString()
//
//    compileOnly("gg.essential:universalcraft-$ucVer:415"){
//        // Setting the attribute to `true` will cause the transformer to apply to this specific artifact
//        attributes { attribute(common, true) }
//    }
//    compileOnly("gg.essential:elementa:710"){
//        attributes { attribute(common, true) }
//    }
//    compileOnly("gg.essential:vigilance:306"){
//        attributes { attribute(common, true) }
//        exclude(group = "gg.essential", module = "elementa")
//    }
//
//    implementation(include("gg.essential:elementa:710")!!)
//    implementation(include("gg.essential:universalcraft-$ucVer:415")!!)
//    implementation(include("gg.essential:vigilance:306")!!)

    //region MOD DEPENDENCIES

    fun modImpl(modPrefix: String, vararg versions: Pair<Int, String?>): Boolean {
        for ((versionMC, versionMod) in versions) {
            if (platform.mcVersion >= versionMC) {
                if (versionMod != null) {
                    modImplementation("$modPrefix$versionMod") {
                        exclude("net.fabricmc.fabric-api")
                        isTransitive = true
                    }
                    return true
                }
                break
            }
        }
        return false
    }

    fun ver(fabric: String?, forge: String?, neoforge: String?): String? = when {
        platform.isFabric -> fabric
        platform.isForge -> forge
        else -> neoforge
    }

    infix fun String.setVar(enabled: Boolean) = preprocess.vars.put(this, if (enabled) 1 else 0)

    "3DSKINLAYERS" setVar modImpl("maven.modrinth:3dskinlayers:",
        26_01_00 to null,
        1_21_02 to ver("R1cL8Kvt", "lmgrljtK",  "9V8JcyMQ"),
        1_21_00 to ver("hRPGTUwZ", "Gx8v5lo4",  "vj8UV3SS"),
        1_20_06 to ver("SrazSQ8a", "CKyYI9pm",  "eSgdmwEr"),
        1_20_04 to ver("S8ohsEt5", "P8ppz86H",  "iGfIHEs9"),
        1_20_02 to ver("v304JX3s", "TRLBDG9K",  "Dit2LPbk"),
        1_20_00 to ver("aiPCdDJa", "sG8E1YEw", null),
        )

    "SODIUM" setVar (
            modImpl("maven.modrinth:sodium:",
                26_01_00 to ver("Amr4VcZo", null, null),
                1_21_05 to ver("fVbw1C7i", null,  "dfyNHRhw"),
                1_21_04 to ver("c3YkZvne", null,  "XgEfENfn"),
                1_21_03 to ver("rLBgU2jc", null,  "M0CXIL7c"),
                1_21_00 to ver("u1OEbNKx", null,  "Pb3OXVqC"),
                1_20_06 to ver("OwLQelEI", null,  null),
                1_20_04 to ver("4GyXKCLd", null,  null),
                1_20_02 to ver("pmgeU5yX", null,  null),
                1_20_00 to ver("ygf8cVZg", null,  null),
            ) or modImpl("maven.modrinth:embeddium:", // forge sodium port
                1_20_02 to null,
                1_20_00 to ver(null, "UTbfe5d1", null),
            )
        )

    "IRIS" setVar (
            modImpl("maven.modrinth:iris:",
                26_01_00 to ver("4cGUAiJ6", null, null),
                //1_21_11 to ver("TSXvi2yD", null,  "t3ruzodq"), //"k9tHcfnb"), //todo why does this break
                1_21_06 to ver("l77DAK6U", null,  "t3ruzodq"), //"xA5cxBvz"), // same here
                1_21_05 to ver("U6evbjd0", null,  "t3ruzodq"), //"KAopiPos"),
                1_21_00 to ver("zsoi0dso", null,  "t3ruzodq"),
                1_20_06 to ver("1bvcmYOc", null,  null),
                1_20_04 to ver("hq98tuSS", null,  null),
                1_20_02 to ver("Cjwm9s3i", null,  null),
                1_20_00 to ver("s5eFLITc", null,  null),
            ) or modImpl("maven.modrinth:oculus:", // forge iris port
                1_20_02 to null,
                1_20_00 to ver(null, "iQ1SwGc3", null),
            )
        )

    "IMMEDIATELYFAST" setVar modImpl("maven.modrinth:immediatelyfast:",
        26_01_00 to null,
        1_21_06 to ver("9JPEk4KN", null, "d8kpGqVx"),
        1_21_05 to ver("kcSoZlE9", null, "43iGBJDV"),
        1_21_00 to ver("giRXGeHH", "yPO020MY", "mWsZ4opk"),
        1_20_06 to ver("ISvVqTAo", "w5Vr5Pxu", "J1qWHyyO"),
        1_20_00 to ver("zJD8Yaa3", "ShWk0wN3", "CiZKgtZH"),
        )

    if (platform.isFabric) {
        modImpl("maven.modrinth:modmenu:",
            26_01_00 to "XIDyVLo7",
            1_21_05 to "R7uVB42W",
            1_21_02 to "PcJvQYqu",
            1_21_00 to "9FL4cmP7",
            1_20_06 to "mtTzRMV2",
            1_20_04 to "sjtVVlsA",
            1_20_02 to "TwfjidT5",
            1_20_00 to "RTFDnTKf",
        )
    }

    //endregion

    if (platform.isNeoForge && mcVersion < 12002) { // NeoForge 20.2.84+ added it themselves
        include("io.github.llamalad7:mixinextras-neoforge:0.4.1:slim")
    }
    if (platform.isForge) {
        compileOnly(annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")!!)
        implementation(include("io.github.llamalad7:mixinextras-forge:0.4.1")!!)
    }

    implementation("com.demonwav.mcdev:annotations:2.1.0")

}

tasks.processResources {
    // Expansions are already set up for `version` (or `file.jarVersion`) and `mcVersionStr`.
    // You do not need to set those up manually.
}

loom {
    // If you need to use a tweaker on legacy (1.12.2 and below) forge:
//    if (platform.isLegacyForge) {
//        launchConfigs.named("client") {
//            arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
//            // And maybe a core mod?
//            property("fml.coreMods.load", "com.example.asm.CoreMod")
//        }
//    }
//    // Mixin on forge? (for legacy forge you will still need to register a tweaker to set up mixin)


    accessWidenerPath = project.parent!!.file("src/main/resources/$accessWidener")
    if (isForge) {
        forge {
            mixinConfig("entity_texture_features.mixins.json")
            // And maybe an access transformer?
            // Though try to avoid these, cause they are not automatically translated to Fabric's access widener
            //accessTransformer(project.parent!!.file("src/main/resources/entity_texture_features.access"))
            convertAccessWideners = true

        }
    }
//    if (isNeoForge) {
//        neoForge { }
//    }
}

loom.noServerRunConfigs()

if (platform.isUnobfuscated) {
    tasks.jar {
        // TODO forge
    }
} else {
    tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
        injectAccessWidener = true
        if (!platform.isFabric) atAccessWideners.add(accessWidener)
    }
}

tasks.processResources {
    inputs.property("project_version", modVersion)
    filesMatching("fabric.mod.json") {
        expand(mapOf(
            "version" to modVersion,
            "access" to accessWidener
        ))
    }
    filesMatching("META-INF/mods.toml") {
        if (platform.isNeoForge || platform.isFabric) {
            exclude()
        } else {
            expand(mapOf("version" to modVersion))
        }
    }
    filesMatching("META-INF/neoforge.mods.toml") {
        if (platform.isFabric || platform.isForge) {
            exclude()
        } else {
            expand(mapOf("version" to modVersion))
            if (platform.isNeoForge && platform.mcVersion < 12005) {
                // NeoForge still uses the old mods.toml name until 1.20.5
                name = "mods.toml"
            }
        }
    }
    filesMatching("entity_texture_features_*.accesswidener") {
        if (this.name != accessWidener || manuallyAccessTransform) this.exclude()
    }
}

tasks.register<Copy>("copyArtifacts") {
    from(layout.buildDirectory.dir("libs").get())
    into("${rootDir}\\jars")
    mustRunAfter(tasks.build)
    delete(layout.buildDirectory.dir("libs").get())
}


tasks.build {
    finalizedBy("copyArtifacts")
}


//region 26.1+ NEOFORGE ACCESS TRANSFORMER GENERATION

//TODO is forge the same? always just relied to architechtury loom for it

val generateAt by tasks.registering {
    val inputAw = rootDir.resolve("src/main/resources/$accessWidener")
    val outputDir = layout.buildDirectory.dir("generated/at")
    val outFile = outputDir.get().file("META-INF/accesstransformer.cfg").asFile
    outFile.delete() // Old file breaks build otherwise

    inputs.file(inputAw)
    outputs.dir(outputDir)

    if (manuallyAccessTransform) doLast {
        val lines = inputAw.absoluteFile.readLines()
        val entries = parseAw(lines)
        val atLines = awToAt(entries)

        outFile.parentFile.mkdirs()
        outFile.writeText(atLines.joinToString("\n"))
    }
}

sourceSets {
    if (manuallyAccessTransform) named("main") {
        resources.srcDir(generateAt)
    }
}

data class AwEntry(
    val type: Type,
    val owner: String,
    val name: String?,
    val desc: String?,
    val access: Access
) {
    enum class Type { CLASS, METHOD, FIELD }
    enum class Access { ACCESSIBLE, EXTENDABLE, MUTABLE }
}

fun parseAw(lines: List<String>): List<AwEntry> {
    return lines
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") && !it.startsWith("accessWidener") }
        .map { line ->
            val parts = line.split(" ")
            val access = when (parts[0]) {
                "accessible" -> AwEntry.Access.ACCESSIBLE
                "extendable" -> AwEntry.Access.EXTENDABLE
                "mutable" -> AwEntry.Access.MUTABLE
                else -> error("Unknown access: ${parts[0]}")
            }

            when (parts[1]) {
                "class" -> AwEntry(
                    AwEntry.Type.CLASS,
                    parts[2],
                    null,
                    null,
                    access
                )
                "method" -> AwEntry(
                    AwEntry.Type.METHOD,
                    parts[2],
                    parts[3],
                    parts[4],
                    access
                )
                "field" -> AwEntry(
                    AwEntry.Type.FIELD,
                    parts[2],
                    parts[3],
                    parts[4],
                    access
                )
                else -> error("Unknown type: ${parts[1]}")
            }
        }
}

fun awToAt(entries: List<AwEntry>): List<String> {
    return entries.mapNotNull { e ->
        val owner = e.owner.replace('/', '.')

        val prefix = when (e.access) {
            AwEntry.Access.ACCESSIBLE -> "public"
            AwEntry.Access.EXTENDABLE -> "public-f"
            AwEntry.Access.MUTABLE -> "public-f"
        }

        when (e.type) {
            AwEntry.Type.CLASS -> "$prefix $owner"
            AwEntry.Type.METHOD -> "$prefix $owner ${e.name}${e.desc}"
            AwEntry.Type.FIELD -> "$prefix $owner ${e.name}"
        }
    }
}
//endregion