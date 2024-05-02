package traben.entity_texture_features;

import com.demonwav.mcdev.annotations.Translatable;
import dev.architectury.injectables.annotations.ExpectPlatform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

@SuppressWarnings({"SameReturnValue", "unused"})
public class ETFVersionDifferenceHandler {

    @ExpectPlatform
    public static boolean isThisModLoaded(String modId) {
        return false;
    }

    @ExpectPlatform
    public static List<String> modsLoaded() {
        return null;
    }

    @ExpectPlatform
    public static File getConfigDir() {
        return null;
    }

    @ExpectPlatform
    public static boolean isForge() {
        return false;
    }

    @ExpectPlatform
    public static boolean isFabric() {
        return false;
    }


    //the below act as handlers for minecraft version differences that have come up during development
    //for instance biome code changed in 1.18.2
    @Nullable
    @ExpectPlatform
    public static String getBiomeString(Level world, BlockPos pos) {
        return null;
    }

    @NotNull
    @ExpectPlatform
    public static Component getTextFromTranslation(@Translatable(foldMethod = true) String translationKey) {
        return Component.nullToEmpty("");
    }

    @NotNull
    @ExpectPlatform
    public static Logger getLogger() {
        return LoggerFactory.getLogger("Entity Texture Features");
    }


}
