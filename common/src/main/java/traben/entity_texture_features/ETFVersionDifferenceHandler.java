package traben.entity_texture_features;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ETFVersionDifferenceHandler {

    @ExpectPlatform
    public static Screen getConfigScreen(Screen parent, boolean isTransparent) {
        return null;
    }

    @ExpectPlatform
    public static boolean isThisModLoaded(String modId) {
        return false;
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

    @ExpectPlatform
    public static boolean areShadersInUse() {
        return false;
    }


    //the below act as handlers for minecraft version differences that have come up during development
    //for instance biome code changed in 1.18.2
    @NotNull
    @ExpectPlatform
    public static String getBiomeString(World world, BlockPos pos) {
        return "";
    }

    @NotNull
    @ExpectPlatform
    public static Text getTextFromTranslation(String translationKey) {
        return Text.of("");
    }

    @NotNull
    @ExpectPlatform
    public static Logger getLogger() {
        return LoggerFactory.getLogger("Entity Texture Features");
    }
}
