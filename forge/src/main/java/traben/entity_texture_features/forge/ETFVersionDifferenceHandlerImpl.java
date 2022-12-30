package traben.entity_texture_features.forge;

import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import traben.entity_texture_features.utils.ETFPlaceholderEntity;

import java.io.File;

import static traben.entity_texture_features.forge.ETFClientForge.ETF_PLACEHOLDER_ENTITY_ENTITY_REGISTRY;

@SuppressWarnings("SameReturnValue")
public class ETFVersionDifferenceHandlerImpl {


    public static boolean isThisModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static File getConfigDir() {
        return FMLPaths.GAMEDIR.get().resolve(FMLPaths.CONFIGDIR.get()).toFile();
    }

    public static boolean isForge() {
        return true;
    }

    public static boolean isFabric() {
        return false;
    }

    public static boolean areShadersInUse() {
        return oculusCompat.isShaderPackInUse();
    }

    public static Logger getLogger() {
        //1.19 & 1.18.2 variation
        return LoggerFactory.getLogger("Entity Texture Features");
    }

    public static Text getTextFromTranslation(String translationKey) {
        //1.18.2 version
        return new TranslatableText(translationKey);
    }

    public static String getBiomeString(World world, BlockPos pos) {
        //1.19 & 1.18.2 variation
        return world.getBiome(pos).getKey().toString().split("\s/\s")[1].replaceAll("[^\\da-zA-Z_:-]", "");
    }


    @NotNull
    public static EntityType<ETFPlaceholderEntity> getPlaceHolderEntityType() {
        // return (EntityType<ETFPlaceholderEntity>) ETFClientForge.ETF_PLACEHOLDER_ENTITY_ENTITY_TYPE;
        return ETF_PLACEHOLDER_ENTITY_ENTITY_REGISTRY.get();
        // return (EntityType<ETFPlaceholderEntity>) ForgeRegistries.ENTITY_TYPES.getValue(new Identifier(ETFClientCommon.MOD_ID + ":etf_placeholder_entity"));
    }
}
