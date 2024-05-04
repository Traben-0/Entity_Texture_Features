package traben.fabric.entity_texture_features;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import traben.entity_texture_features.ETF;

import java.io.File;
import java.util.List;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ETFClientFabric implements ClientModInitializer {




    @Override
    public void onInitializeClient() {
        ETF.start(
                ETFClientFabric::isThisModLoaded,
                ETFClientFabric::modsLoaded,
                ETFClientFabric::getConfigDir,
                ETFClientFabric::isForge,
                ETFClientFabric::isFabric);
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


    public static List<String> modsLoaded() {
        return FabricLoader.getInstance().getAllMods().stream().map(modContainer -> modContainer.getMetadata().getId()).toList();
    }
}
