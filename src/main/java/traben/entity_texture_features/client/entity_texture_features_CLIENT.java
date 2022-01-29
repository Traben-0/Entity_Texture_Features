package traben.entity_texture_features.client;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class entity_texture_features_CLIENT implements ModInitializer {
    //0 = vanilla only    1+ is zombie1+.png
    public static Map<String,Integer> Texture_TotalTrueRandom = new HashMap<String, Integer>() ;
    public static Map<UUID,Integer> UUID_randomTextureSuffix = new HashMap<UUID, Integer>() ;
    public static Map<String, ArrayList<randomCase>> Texture_OptifineRandomSettingsPerTexture = new HashMap<String,ArrayList<randomCase> >() ;
    public static Map<String,Boolean> Texture_OptifineOrTrueRandom = new HashMap<String, Boolean>() ;
    public static Map<String,Integer> optifineOldOrVanilla = new HashMap<String, Integer>() ;// 0,1,2
    public static Map<String,Boolean> ignoreOnePNG = new HashMap<String, Boolean>() ;
    public static String emissiveSuffix = null;
    public static Map<String, Identifier> Texture_Emissive = new HashMap<String, Identifier>() ;
    public static boolean irisDetected = false;
    public static boolean puzzleDetected = false;
    @Override
    public void onInitialize() {

    }
}
