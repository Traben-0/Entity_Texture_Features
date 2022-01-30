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
    public static Map<String,Integer> Texture_TotalTrueRandom = new HashMap<>() ;
    public static Map<UUID,Integer> UUID_randomTextureSuffix = new HashMap<>() ;
    public static ArrayList<UUID> UUID_entityAlreadyCalculated = new ArrayList<>() ;//
    public static Map<UUID,Long> UUID_entityAwaitingDataClearing = new HashMap<>();
    public static Map<String, ArrayList<randomCase>> Texture_OptifineRandomSettingsPerTexture = new HashMap<>() ;
    public static Map<String,Boolean> Texture_OptifineOrTrueRandom = new HashMap<>() ;
    public static Map<String,Integer> optifineOldOrVanilla = new HashMap<>() ;// 0,1,2
    public static Map<String,Boolean> ignoreOnePNG = new HashMap<>() ;
    public static String emissiveSuffix = null;
    public static Map<String, Identifier> Texture_Emissive = new HashMap<>() ;
    public static boolean irisDetected = false;
    public static boolean puzzleDetected = false;
    @Override
    public void onInitialize() {
        System.out.println("Entity Texture Features - Loading");
    }
}
