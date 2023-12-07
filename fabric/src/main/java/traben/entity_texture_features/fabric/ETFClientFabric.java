package traben.entity_texture_features.fabric;

import net.fabricmc.api.ClientModInitializer;
import traben.entity_texture_features.ETFClientCommon;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ETFClientFabric implements ClientModInitializer {




    @Override
    public void onInitializeClient() {
        ETFClientCommon.start();
    }
}
