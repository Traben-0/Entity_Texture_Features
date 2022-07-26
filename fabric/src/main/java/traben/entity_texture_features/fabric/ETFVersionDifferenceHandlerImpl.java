package traben.entity_texture_features.fabric;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.fabric.config.ETFConfigScreenFabric;

import java.io.File;
import java.util.Objects;

public class ETFVersionDifferenceHandlerImpl {
    public static Screen getConfigScreen(Screen parent, boolean isTransparent) {
        return ETFConfigScreenFabric.getConfigScreen(parent,isTransparent);
    }

    public static boolean isThisModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static File getConfigDir() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    public static boolean isForge() {
        return false;
    }

    public static boolean isFabric() {
        return true;
    }

    public static boolean areShadersInUse() {
        return IrisCompat.isShaderPackInUse();
    }

    public static Logger getLogger() {
        //1.19 & 1.18.2 variation
        return LogManager.getLogger("Entity Texture Features");
    }

    public static Text getTextFromTranslation(String translationKey) {
        //1.18.2 version
        return new TranslatableText(translationKey);
    }

    @NotNull
    public static String getBiomeString(World world, BlockPos pos) {
        //1.17 and before version
        return Objects.requireNonNull(world.getRegistryManager().get(Registry.BIOME_KEY).getId(world.getBiome(pos))).toString();
    }
}
