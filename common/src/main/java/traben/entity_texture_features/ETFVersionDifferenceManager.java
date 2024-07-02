package traben.entity_texture_features;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;
import java.util.List;

public class ETFVersionDifferenceManager {

    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isThisModLoaded(String modId) {
        throw new AssertionError();
    }


    @ExpectPlatform
    public static boolean isForge() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }
    @ExpectPlatform
    public static List<String> modsLoaded() {
        throw new AssertionError();
    }


}
