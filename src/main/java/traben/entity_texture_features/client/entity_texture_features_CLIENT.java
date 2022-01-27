package traben.entity_texture_features.client;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class entity_texture_features_CLIENT implements ModInitializer {
    public static Map<UUID,Boolean> UUID_isRandom = new HashMap<UUID, Boolean>() ;
    //[0] is vanilla [1] is new
    //public static Map<UUID,Integer> UUID_randomTextureSuffix = new HashMap<UUID, Integer>() ;
    public static Map<UUID,Identifier> UUID_randomTexture = new HashMap<UUID, Identifier>() ;

    public static String emissiveSuffix = null;

    public static Map<String, Identifier> Texture_Emissive = new HashMap<String, Identifier>() ;
    @Override
    public void onInitialize() {

    }
}
