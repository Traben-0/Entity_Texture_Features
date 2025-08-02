import com.replaymod.gradle.preprocess.Node


plugins {
    // This marks the current project as the root of a multi-version project.
    // Any project using `gg.essential.multi-version` must have a parent with this root plugin applied.
    // Advanced users may use multiple (potentially independent) multi-version trees in different sub-projects.
    // This is currently equivalent to applying `com.replaymod.preprocess-root`.
    kotlin("jvm") version "2.0.0" apply false
    id("gg.essential.loom") version "1.9.32" apply false // https://repo.essential.gg/#/public/gg/essential/loom/gg.essential.loom.gradle.plugin
    id("gg.essential.multi-version.root")
}





preprocess {
    // Here you first need to create a node per version you support and assign it an integer Minecraft version.
    // The mappings value is currently meaningless.

    strictExtraMappings.set(true)


    fun Int.formatVersionNumber(): String {
        val str = this.toString()
        val part2 = str.substring(1, 3)
        val part3 = str.substring(3, 5).trimStart('0')
        return "${str[0]}.$part2${if (part3.isNotEmpty()) ".$part3" else ""}"
    }

    fun Node?.connectToVersion(mcVersion: Int, forge: Boolean = true, neoforge: Boolean = true): Node {
        val verString = mcVersion.formatVersionNumber()

        val fabric = createNode("$verString-fabric", mcVersion, "mojmap")
        this?.let { fabric.link(it) }

        if (forge) {
            createNode("$verString-forge", mcVersion, "srg")
            .link(fabric)
        }
        if (neoforge) {
            createNode("$verString-neoforge", mcVersion, "mojmap")
            .link(fabric)
        }

        return fabric
    }


    val current = null.connectToVersion(12106)

    // next, then remap the main project to this and set the current to old
    //current.connectToVersion(12109, forge = false, neoforge = false)

    // older
    current.connectToVersion(12105)
        .connectToVersion(12104)
        .connectToVersion(12103) // would normally do 12102 to have the lowest compatible version but forge 1.21.2 doesn't exist
        .connectToVersion(12100)
        .connectToVersion(12006)
        .connectToVersion(12004)
        .connectToVersion(12002)
        .connectToVersion(12001, neoforge = false)

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