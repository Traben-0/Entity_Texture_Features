package traben.entity_texture_features.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import traben.entity_texture_features.ETF;

import java.util.List;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ETFClientFabric implements ClientModInitializer {




    @Override
    public void onInitializeClient() {
        ETF.start(
                ETFClientFabric::isThisModLoaded,
                ETFClientFabric::modsLoaded,
                FabricLoader.getInstance().getConfigDir().toFile(),
                false);
    }

    public static boolean isThisModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static List<String> modsLoaded() {
        return FabricLoader.getInstance().getAllMods().stream().map(modContainer -> modContainer.getMetadata().getId()).toList();
    }
}
