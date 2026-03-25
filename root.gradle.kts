import com.replaymod.gradle.preprocess.Node


plugins {
    // This marks the current project as the root of a multi-version project.
    // Any project using `gg.essential.multi-version` must have a parent with this root plugin applied.
    // Advanced users may use multiple (potentially independent) multi-version trees in different sub-projects.
    // This is currently equivalent to applying `com.replaymod.preprocess-root`.
    kotlin("jvm") version "2.3.0" apply false
    id("gg.essential.loom") version "1.15.48" apply false // https://repo.essential.gg/#/public/gg/essential/loom/gg.essential.loom.gradle.plugin
    id("gg.essential.multi-version.root")
}





preprocess {
    // Here you first need to create a node per version you support and assign it an integer Minecraft version.
    // The mappings value is currently meaningless.

    strictExtraMappings.set(true)


    fun Int.formatVersionNumber(): String {
        val str = this.toString()
        val l = str.length
        val major = str.substring((l - 6).coerceAtLeast(0), l - 4)
        val minor = str.substring(l - 4, l - 2).trimStart('0')
        val patch = str.substring(l - 2, l).trimStart('0')
        return "$major.$minor${if (patch.isNotEmpty()) ".$patch" else ""}"
    }

    fun Node?.connectToVersion(mcVersion: Int, forge: Boolean = true, neoforge: Boolean = true): Node {
        val verString = mcVersion.formatVersionNumber()

        // Issue: these need to be created before fabric's node is, only affects 26.1+
        val forgeNode = if (forge) createNode("$verString-forge", mcVersion, "srg") else null
        val neoforgeNode = if (neoforge) createNode("$verString-neoforge", mcVersion, "mojmap") else null

        val fabricNode = createNode("$verString-fabric", mcVersion, "mojmap")

        forgeNode?.link(fabricNode)
        neoforgeNode?.link(fabricNode)

        this?.let {
            val itVerStr = it.mcVersion.formatVersionNumber()
            val file = projectDir.resolve("versions/$itVerStr-$verString.txt")
            it.link(fabricNode, file.takeIf(File::exists))
        }

        return fabricNode
    }

    // next, then remap the main project to this and set the current to old
    //current.connectToVersion(12109, forge = false, neoforge = false)
//    val n = createNode("26.1-neoforge", 26_01_00, "mojmap")
//    val fabric = createNode("26.1-fabric", 26_01_00, "mojmap")
//        fabric.link(n)

    null.connectToVersion(26_01_00, forge = false, neoforge = true)
        .connectToVersion(1_21_11)
        .connectToVersion(1_21_09)
        .connectToVersion(1_21_06)
        .connectToVersion(1_21_05)
        .connectToVersion(1_21_04)
        .connectToVersion(1_21_03) // would normally do 12102 to have the lowest compatible version but forge 1.21.2 doesn't exist
        .connectToVersion(1_21_00)
        .connectToVersion(1_20_06)
        .connectToVersion(1_20_04)
        .connectToVersion(1_20_02)
        .connectToVersion(1_20_01, neoforge = false)

    // And then you need to tell the preprocessor which versions it should directly convert between.
    // This should form a directed graph with no cycles (i.e. a tree), which the preprocessor will then traverse to
    // produce source code for all versions from the main version.
    // Do note that the preprocessor can only convert between two projects when they are either on the same Minecraft
    // version (but use different mappings, e.g. 1.16.2 forge to fabric), or when they are using the same intermediary
    // mappings (but on different Minecraft versions, e.g. 1.12.2 forge to 1.8.9 forge, or 1.16.2 fabric to 1.18 fabric)
    // but not both at the same time, i.e. you cannot go straight from 1.12.2 forge to 1.16.2 fabric, you need to go via
    // an intermediary 1.16.2 forge project which has something in common with both.
    // For any link, you can optionally specify a file containing extra mappings which the preprocessor cannot infer by
    // itself, e.g. forge intermediary names do not contain class names, so you may need to supply mappings for those
    // manually.
//    forge11202.link(forge10809, file("versions/1.12.2-1.8.9.txt"))
}