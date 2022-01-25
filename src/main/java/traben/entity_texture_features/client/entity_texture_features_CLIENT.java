package traben.entity_texture_features.client;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class entity_texture_features_CLIENT implements ModInitializer {
    public static Map<UUID,Integer[]> randomData = new HashMap<UUID, Integer[]>() ;

    public static Map<String, Identifier> hasEmissive = new HashMap<String, Identifier>() ;
    @Override
    public void onInitialize() {

    }
}
