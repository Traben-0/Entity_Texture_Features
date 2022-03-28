package traben.entity_texture_features.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

public interface ETF_METHODS {

    //checks if files exists and is in the same or higher resourcepack as id 2
    default boolean ETF_isExistingFileAndSameOrHigherResourcepackAs(Identifier id, Identifier vanillaIdToMatch) {
        if (ETF_isExistingFile(id)) {
            try {
                ResourceManager resource = MinecraftClient.getInstance().getResourceManager();
                String packname = resource.getResource(id).getResourcePackName();
                String packname2 = resource.getResource(vanillaIdToMatch).getResourcePackName();
                if (packname.equals(packname2)) {
                    return true;
                } else {
                    for (ResourcePack pack :
                            resource.streamResourcePacks().toList()) {
                        //loops through all resourcepacks from bottom "default" to top
                        if (packname.equals(pack.getName())) {
                            //if first id is reached first it is lower and must be false
                            return false;
                        }
                        if (packname2.equals(pack.getName())) {
                            //if the second file is reached first it must be lower thus id 1 is higher return true
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                //
            }
        }
        return false;
    }


    default boolean ETF_isExistingFile(Identifier id) {
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id);
            try {
                resource.getInputStream();
                resource.close();
                return true;
            } catch (IOException e) {
                resource.close();
                return false;
            }
        } catch (IOException f) {
            return false;
        }
    }

    private boolean ETF_checkPathExistAndSameOrHigherResourcepackAs(String path, String path2) {
        return ETF_isExistingFileAndSameOrHigherResourcepackAs(new Identifier(path), new Identifier(path2));
    }

    default void ETF_resetVisuals() {
        ETF_modMessage("Reloading...", false);
        ETF_PATH_TotalTrueRandom.clear();

        ETF_UUID_randomTextureSuffix.clear();
        ETF_UUID_randomTextureSuffix2.clear();
        ETF_UUID_randomTextureSuffix3.clear();
        ETF_UUID_randomTextureSuffix4.clear();
        ETF_UUID_hasUpdatableRandomCases.clear();
        ETF_UUID_hasUpdatableRandomCases2.clear();
        ETF_UUID_hasUpdatableRandomCases3.clear();
        ETF_UUID_hasUpdatableRandomCases4.clear();

        ETF_PATH_OptifineRandomSettingsPerTexture.clear();
        ETF_PATH_OptifineOrTrueRandom.clear();
        ETF_PATH_OptifineOldVanillaETF_0123.clear();// 0,1,2
        ETF_PATH_ignoreOnePNG.clear();
        ETF_UUID_entityAlreadyCalculated.clear();//only time it clears
        ETF_UUID_entityAwaitingDataClearing.clear();
        ETF_UUID_entityAwaitingDataClearing2.clear();

        ETF_UUID_OriginalNonUpdatePropertyStrings.clear();

        ETF_UUID_playerHasFeatures.clear();
        ETF_UUID_playerHasEnchant.clear();
        ETF_UUID_playerHasEmissive.clear();
        ETF_UUID_playerTransparentSkinId.clear();
        ETF_UUID_playerSkinDownloadedYet.clear();
        for (HttpURLConnection h :
                ETF_URL_HTTPtoDisconnect1.values()) {
            if (h != null) {
                h.disconnect();
            }
        }
        for (HttpURLConnection h :
                ETF_URL_HTTPtoDisconnect2.values()) {
            if (h != null) {
                h.disconnect();
            }
        }
        ETF_UUID_playerHasCoat.clear();
        ETF_URL_HTTPtoDisconnect1.clear();

        ETF_PATH_FailedPropertiesToIgnore.clear();

        ETF_PATH_HasBlink.clear();
        ETF_PATH_HasBlink2.clear();

        ETF_UUID_TridentName.clear();

        ETF_PATH_EmissiveTextureIdentifier.clear();
        ETF_setEmissiveSuffix();

        ETF_PATH_VillagerIsExistingFeature.clear();

        ETF_mooshroomRedCustomShroom = 0;
        ETF_mooshroomBrownCustomShroom = 0;

        ETF_registerNativeImageToIdentifier(ETF_emptyNativeImage(1, 1), "etf:blank.png");
    }

    default void ETF_resetSingleData(UUID id) {
        ETF_UUID_randomTextureSuffix.remove(id);
        ETF_UUID_randomTextureSuffix2.remove(id);
        ETF_UUID_randomTextureSuffix3.remove(id);
        ETF_UUID_randomTextureSuffix4.remove(id);


    }

    default Properties ETF_readProperties(String path) {
        return ETF_readProperties(path, null);
    }

    default Properties ETF_readProperties(String path, String pathOfTextureToUseForResourcepackCheck) {
        Properties props = new Properties();
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(path));
            //skip if needs to be same resourcepack
            if (pathOfTextureToUseForResourcepackCheck != null) {
                ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
                String packname1 = resource.getResourcePackName();
                String packname2 = resourceManager.getResource(new Identifier(pathOfTextureToUseForResourcepackCheck)).getResourcePackName();
                if (!packname1.equals(packname2)) {
                    //not same pack check it is a higher pack and only continue if packname1 is higher
                    for (ResourcePack pack :
                            resourceManager.streamResourcePacks().toList()) {
                        //loops through all resourcepacks from bottom "default" to top
                        if (packname1.equals(pack.getName())) {
                            //if first id is reached first it is lower and must not be used return null
                            return null;
                        }
                        if (packname2.equals(pack.getName())) {
                            //if the second file is reached first it must be lower thus id 1 is higher so break to continue
                            break;
                        }
                    }
                }
            }
            try {
                InputStream in = resource.getInputStream();
                props.load(in);
                resource.close();
            } catch (Exception e) {
                resource.close();
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        // Example return
        // {skins.4=3, skins.5=1-3, skins.2=2, skins.3=3, weights.5=1 1 , biomes.2=desert, health.3=1-50%, names.4=iregex:mob name.*}
        return props;
    }

    default NativeImage ETF_getNativeImageFromID(Identifier identifier) {
        NativeImage img;
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(identifier);
            try {
                InputStream in = resource.getInputStream();
                img = NativeImage.read(in);
                resource.close();
            } catch (Exception e) {
                resource.close();
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return img;
    }


    default void ETF_processNewRandomTextureCandidate(String vanillaTexturePath) {
        boolean hasProperties = false;
        String properties = "";
        //set default incase of no change
        ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaTexturePath,2);
        if (ETF_checkPathExistAndSameOrHigherResourcepackAs(vanillaTexturePath.replace(".png", ".properties").replace("textures", "etf/random"), vanillaTexturePath.replace(".png", "2.png").replace("textures", "etf/random"))) {
            properties = vanillaTexturePath.replace(".png", ".properties").replace("textures", "etf/random");
            hasProperties = true;
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaTexturePath, 3);
        } else if (ETF_isExistingFile(new Identifier(vanillaTexturePath.replace(".png", "2.png").replace("textures", "etf/random")))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaTexturePath, 3);
        } else if (ETF_checkPathExistAndSameOrHigherResourcepackAs(vanillaTexturePath.replace(".png", ".properties").replace("textures", "optifine/random"), vanillaTexturePath.replace(".png", "2.png").replace("textures", "optifine/random"))) {
            properties = vanillaTexturePath.replace(".png", ".properties").replace("textures", "optifine/random");
            hasProperties = true;
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaTexturePath, 0);
        } else if (ETF_isExistingFile(new Identifier(vanillaTexturePath.replace(".png", "2.png").replace("textures", "optifine/random")))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaTexturePath, 0);
        } else if (ETF_checkPathExistAndSameOrHigherResourcepackAs(vanillaTexturePath.replace(".png", ".properties").replace("textures/entity", "optifine/mob"), vanillaTexturePath.replace(".png", "2.png").replace("textures/entity", "optifine/mob"))) {
            properties = vanillaTexturePath.replace(".png", ".properties").replace("textures/entity", "optifine/mob");
            hasProperties = true;
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaTexturePath, 1);
        } else if (ETF_isExistingFile(new Identifier(vanillaTexturePath.replace(".png", "2.png").replace("textures/entity", "optifine/mob")))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaTexturePath, 1);
        } else if (ETF_checkPathExistAndSameOrHigherResourcepackAs(vanillaTexturePath.replace(".png", ".properties"), vanillaTexturePath.replace(".png", "2.png"))) {
            properties = vanillaTexturePath.replace(".png", ".properties");
            hasProperties = true;
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaTexturePath, 2);
        } else if (ETF_isExistingFile(new Identifier(vanillaTexturePath.replace(".png", "2.png")))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaTexturePath, 2);
        }

        //no settings just true random
        if (hasProperties && !ETF_PATH_FailedPropertiesToIgnore.contains(properties)) {//optifine settings found
            ETF_processOptifineTextureCandidate(vanillaTexturePath, properties);
        } else {
            ETF_processTrueRandomCandidate(vanillaTexturePath);
        }
    }

    private void ETF_processOptifineTextureCandidate(String vanillaTexturePath, String propertiesPath) {
        try {
            ETF_PATH_ignoreOnePNG.put(vanillaTexturePath, !(ETF_isExistingFile(new Identifier(propertiesPath.replace(".properties", "1.png")))));

            String twoPngPath = ETF_returnOptifineOrVanillaPath(vanillaTexturePath, 2, "");
            Properties props = ETF_readProperties(propertiesPath, twoPngPath);

            if (props != null) {
                Set<String> propIds = props.stringPropertyNames();
                //set so only 1 of each
                Set<Integer> numbers = new HashSet<>();
                //get the numbers we are working with
                for (String str :
                        propIds) {
                    numbers.add(Integer.parseInt(str.replaceAll("[^0-9]", "")));
                }
                //sort from lowest to largest
                List<Integer> numbersList = new ArrayList<>(numbers);
                Collections.sort(numbersList);
                ArrayList<randomCase> allCasesForTexture = new ArrayList<>();
                for (Integer num :
                        numbersList) {
                    //loops through each known number in properties
                    //all of case 1 ect should be processed here
                    Integer[] suffixes = {};
                    Integer[] weights = {};
                    String[] biomes = {};
                    Integer[] heights = {};
                    String[] names = {};
                    String[] professions = {};
                    String[] collarColours = {};
                    int baby = 0; // 0 1 2 - dont true false
                    int weather = 0; //0,1,2,3 - no clear rain thunder
                    String[] health = {};
                    Integer[] moon = {};
                    String[] daytime = {};
                    String[] blocks = {};
                    String[] teams = {};

                    if (props.containsKey("skins." + num) || props.containsKey("textures." + num)) {
                        String dataFromProps = props.containsKey("skins." + num) ? props.getProperty("skins." + num).trim() : props.getProperty("textures." + num).trim();
                        String[] skinData = dataFromProps.split("\s+");
                        ArrayList<Integer> suffixNumbers = new ArrayList<>();
                        for (String data :
                                skinData) {
                            //check if range
                            data = data.trim();
                            if (!data.replaceAll("[^0-9]", "").isEmpty()) {
                                if (data.contains("-")) {
                                    suffixNumbers.addAll(Arrays.asList(ETF_getIntRange(data)));
                                } else {
                                    suffixNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                                }
                            }
                        }
                        suffixes = suffixNumbers.toArray(new Integer[0]);
                    }
                    if (props.containsKey("weights." + num)) {
                        String dataFromProps = props.getProperty("weights." + num).trim();
                        String[] weightData = dataFromProps.split("\s+");
                        ArrayList<Integer> builder = new ArrayList<>();
                        for (String s :
                                weightData) {
                            s = s.trim();
                            if (!s.replaceAll("[^0-9]", "").isEmpty()) {
                                builder.add(Integer.parseInt(s.replaceAll("[^0-9]", "")));
                            }
                        }
                        weights = builder.toArray(new Integer[0]);
                    }
                    if (props.containsKey("biomes." + num)) {
                        String dataFromProps = props.getProperty("biomes." + num).trim();
                        biomes = dataFromProps.toLowerCase().split("\s+");
                    }
                    //add legacy height support
                    if (!props.containsKey("heights." + num) && (props.containsKey("minHeight." + num) || props.containsKey("maxHeight." + num))) {
                        String min = "-64";
                        String max = "319";
                        if (props.containsKey("minHeight." + num)) {
                            min = props.getProperty("minHeight." + num).trim();
                        }
                        if (props.containsKey("maxHeight." + num)) {
                            max = props.getProperty("maxHeight." + num).trim();
                        }
                        props.put("heights." + num, min + "-" + max);
                    }
                    if (props.containsKey("heights." + num)) {
                        String dataFromProps = props.getProperty("heights." + num).trim();
                        String[] heightData = dataFromProps.split("\s+");
                        ArrayList<Integer> heightNumbers = new ArrayList<>();
                        for (String data :
                                heightData) {
                            //check if range
                            data = data.trim();
                            if (!data.replaceAll("[^0-9]", "").isEmpty()) {
                                if (data.contains("-")) {
                                    heightNumbers.addAll(Arrays.asList(ETF_getIntRange(data)));
                                } else {
                                    heightNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                                }
                            }
                        }
                        heights = heightNumbers.toArray(new Integer[0]);
                    }

                    if (props.containsKey("names." + num) || props.containsKey("name." + num)) {
                        String dataFromProps = props.containsKey("name." + num) ? props.getProperty("name." + num).trim() : props.getProperty("names." + num).trim();
                        if (dataFromProps.contains("regex:") || dataFromProps.contains("pattern:")) {
                            names = new String[]{dataFromProps};
                        } else {
                            //names = dataFromProps.split("\s+");
                            //allow    "multiple names" among "other"
                            List<String> list = new ArrayList<>();
                            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(dataFromProps);
                            while (m.find()) {
                                list.add(m.group(1).replace("\"", "").trim());
                            }
                            names = list.toArray(new String[0]);
                        }
                    }
                    if (props.containsKey("professions." + num)) {
                        professions = props.getProperty("professions." + num).trim().split("\s+");
                    }
                    if (props.containsKey("collarColors." + num) || props.containsKey("collarColours." + num)) {
                        collarColours = props.containsKey("collarColors." + num) ? props.getProperty("collarColors." + num).trim().split("\s+") : props.getProperty("collarColours." + num).trim().split("\s+");
                    }
                    if (props.containsKey("baby." + num)) {
                        String dataFromProps = props.getProperty("baby." + num).trim();
                        switch (dataFromProps) {
                            case "true" -> baby = 1;
                            case "false" -> baby = 2;
                        }
                    }
                    if (props.containsKey("weather." + num)) {
                        String dataFromProps = props.getProperty("weather." + num).trim();
                        switch (dataFromProps) {
                            case "clear" -> weather = 1;
                            case "rain" -> weather = 2;
                            case "thunder" -> weather = 3;
                        }
                    }
                    if (props.containsKey("health." + num)) {
                        health = props.getProperty("health." + num).trim().split("\s+");
                    }
                    if (props.containsKey("moonPhase." + num)) {
                        String dataFromProps = props.getProperty("moonPhase." + num).trim();
                        String[] moonData = dataFromProps.split("\s+");
                        ArrayList<Integer> moonNumbers = new ArrayList<>();
                        for (String data :
                                moonData) {
                            //check if range
                            data = data.trim();
                            if (!data.replaceAll("[^0-9]", "").isEmpty()) {
                                if (data.contains("-")) {
                                    moonNumbers.addAll(Arrays.asList(ETF_getIntRange(data)));
                                } else {
                                    moonNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                                }
                            }
                        }
                        moon = moonNumbers.toArray(new Integer[0]);
                    }
                    if (props.containsKey("dayTime." + num)) {
                        daytime = props.getProperty("dayTime." + num).trim().split("\s+");
                    }
                    if (props.containsKey("blocks." + num)) {
                        blocks = props.getProperty("blocks." + num).trim().split("\s+");
                    } else if (props.containsKey("block." + num)) {
                        blocks = props.getProperty("block." + num).trim().split("\s+");
                    }
                    if (props.containsKey("teams." + num)) {
                        String teamData = props.getProperty("teams." + num).trim();
                        List<String> list = new ArrayList<>();
                        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(teamData);
                        while (m.find()) {
                            list.add(m.group(1).replace("\"", ""));
                        }
                        teams = list.toArray(new String[0]);
                    } else if (props.containsKey("team." + num)) {
                        String teamData = props.getProperty("team." + num).trim();
                        List<String> list = new ArrayList<>();
                        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(teamData);
                        while (m.find()) {
                            list.add(m.group(1).replace("\"", ""));
                        }
                        teams = list.toArray(new String[0]);
                    }

                    if (suffixes.length != 0) {
                        allCasesForTexture.add(new randomCase(suffixes, weights, biomes, heights, names, professions, collarColours, baby, weather, health, moon, daytime, blocks, teams));
                    }
                }
                if (!allCasesForTexture.isEmpty()) {
                    ETF_PATH_OptifineRandomSettingsPerTexture.put(vanillaTexturePath, allCasesForTexture);
                    ETF_PATH_OptifineOrTrueRandom.put(vanillaTexturePath, true);
                } else {
                    ETF_modMessage("Ignoring properties file that failed to load any cases @ " + propertiesPath, false);
                    ETF_PATH_FailedPropertiesToIgnore.add(propertiesPath);
                }
            } else {//properties file is null
                ETF_modMessage("Ignoring properties file that was null @ " + propertiesPath, false);
                ETF_PATH_FailedPropertiesToIgnore.add(propertiesPath);
            }
        } catch (Exception e) {
            ETF_modMessage("Ignoring properties file that caused Exception @ " + propertiesPath + e, false);
            ETF_PATH_FailedPropertiesToIgnore.add(propertiesPath);
        }
    }

    default Integer[] ETF_getIntRange(String rawRange) {
        //assume rawRange =  "20-56"  but can be "-64-56"  or "-14"
        rawRange = rawRange.trim();
        //sort negatives before split
        if (rawRange.startsWith("-")) {
            rawRange = rawRange.replaceFirst("-", "N");
        }
        rawRange = rawRange.replaceAll("--", "-N");
        String[] split = rawRange.split("-");
        if (split.length > 1) {//sort out range
            int[] minMax = {Integer.parseInt(split[0].replaceAll("[^0-9]", "")), Integer.parseInt(split[1].replaceAll("[^0-9]", ""))};
            if (split[0].contains("N")) {
                minMax[0] = -minMax[0];
            }
            if (split[1].contains("N")) {
                minMax[1] = -minMax[1];
            }
            ArrayList<Integer> builder = new ArrayList<>();
            if (minMax[0] > minMax[1]) {
                //0 must be smaller
                minMax = new int[]{minMax[1], minMax[0]};
            }
            if (minMax[0] < minMax[1]) {
                for (int i = minMax[0]; i <= minMax[1]; i++) {
                    builder.add(i);
                }
            } else {
                ETF_modMessage("Optifine properties failed to load: Texture heights range has a problem in properties file. this has occurred for value \"" + rawRange.replace("N", "-") + "\"", false);
            }
            return builder.toArray(new Integer[0]);
        } else {//only 1 number but method ran because of "-" present
            if (split[0].contains("N")) {
                return new Integer[]{-Integer.parseInt(split[0].replaceAll("[^0-9]", ""))};
            } else {
                return new Integer[]{Integer.parseInt(split[0].replaceAll("[^0-9]", ""))};
            }

        }
    }

    default void ETF_testCases(String vanillaPath, UUID id, Entity entity, boolean isUpdate) {
        ETF_testCases(vanillaPath, id, entity, isUpdate, ETF_UUID_randomTextureSuffix, ETF_UUID_hasUpdatableRandomCases);
    }

    default void ETF_testCases(String vanillaPath, UUID id, Entity entity, boolean isUpdate, HashMap<UUID, Integer> UUID_RandomSuffixMap, HashMap<UUID, Boolean> UUID_CaseHasUpdateablesCustom) {
        for (randomCase test :
                ETF_PATH_OptifineRandomSettingsPerTexture.get(vanillaPath)) {

            //skip if its only an update and case is not updatable
            if (test.testEntity((LivingEntity) entity, ETF_UUID_entityAlreadyCalculated.contains(id), UUID_CaseHasUpdateablesCustom)) {
                UUID_RandomSuffixMap.put(id, test.getWeightedSuffix(id, ETF_PATH_ignoreOnePNG.get(vanillaPath)));
                Identifier tested = ETF_returnOptifineOrVanillaIdentifier(vanillaPath, UUID_RandomSuffixMap.get(id));

                if (!ETF_isExistingFile(tested) && !isUpdate) {
                    UUID_RandomSuffixMap.put(id, 0);
                }
                break;
            }
        }
        if (!UUID_CaseHasUpdateablesCustom.containsKey(id))
            UUID_CaseHasUpdateablesCustom.put(id, false);
    }

    default void ETF_modMessage(String message, boolean inChat) {
        if (inChat) {
            ClientPlayerEntity plyr = MinecraftClient.getInstance().player;
            if (plyr != null) {
                plyr.sendMessage(Text.of("\u00A76[Entity Texture Features]\u00A77: " + message), false);
            } else {
                LogManager.getLogger().info("[Entity Texture Features]: " + message);
            }
        } else {
            LogManager.getLogger().info("[Entity Texture Features]: " + message);
        }
    }

    default String ETF_returnOptifineOrVanillaPath(String vanillaPath, int randomId, String emissiveSuffx) {


        String append = (randomId == 0 ? "" : randomId) + emissiveSuffx + ".png";
        if (ETF_PATH_OptifineOldVanillaETF_0123.containsKey(vanillaPath)) {
            return switch (ETF_PATH_OptifineOldVanillaETF_0123.get(vanillaPath)) {
                case 0 -> vanillaPath.replace(".png", append).replace("textures", "optifine/random");
                case 1 -> vanillaPath.replace(".png", append).replace("textures/entity", "optifine/mob");
                case 3 -> vanillaPath.replace(".png", append).replace("textures", "etf/random");
                default -> vanillaPath.replace(".png", append);
            };
        }else{
            return vanillaPath;
        }
    }

    default Identifier ETF_returnOptifineOrVanillaIdentifier(String vanillaPath, int randomId) {
        return new Identifier(ETF_returnOptifineOrVanillaPath(vanillaPath, randomId, ""));
    }

    default Identifier ETF_returnOptifineOrVanillaIdentifier(String vanillaPath) {
        return new Identifier(ETF_returnOptifineOrVanillaPath(vanillaPath, 0, ""));
    }

    private void ETF_processTrueRandomCandidate(String vanillaPath) {
        ETF_PATH_ignoreOnePNG.put(vanillaPath, true);
        boolean keepGoing = false;
        //first iteration longer
        int successCount = 0;
        //allTextures.add(vanillaPath);
        //can start from either texture1.png or texture2.png check both first
        //check if texture1.png is used
        String checkPath = vanillaPath.replace(".png", "1.png");
        String checkPathOldRandomFormat = vanillaPath.replace(".png", "1.png").replace("textures/entity", "optifine/mob");
        String checkPathOptifineFormat = vanillaPath.replace(".png", "1.png").replace("textures", "optifine/random");
        String checkPathETFFormat = vanillaPath.replace(".png", "1.png").replace("textures", "etf/random");
        if (ETF_isExistingFile(new Identifier(checkPathETFFormat))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaPath, 3);
            ETF_PATH_ignoreOnePNG.put(vanillaPath, false);
            //successCount++;
        } else if (ETF_isExistingFile(new Identifier(checkPathOptifineFormat))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaPath, 0);
            ETF_PATH_ignoreOnePNG.put(vanillaPath, false);
            //successCount++;
        } else if (ETF_isExistingFile(new Identifier(checkPathOldRandomFormat))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaPath, 1);
            ETF_PATH_ignoreOnePNG.put(vanillaPath, false);
            //successCount++;
        } else if (ETF_isExistingFile(new Identifier(checkPath))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaPath, 2);
            ETF_PATH_ignoreOnePNG.put(vanillaPath, false);
            //successCount++;
        }

        //check if texture 2.png is used
        checkPath = vanillaPath.replace(".png", "2.png");
        checkPathOldRandomFormat = vanillaPath.replace(".png", "2.png").replace("textures/entity", "optifine/mob");
        checkPathOptifineFormat = vanillaPath.replace(".png", "2.png").replace("textures", "optifine/random");
        checkPathETFFormat = vanillaPath.replace(".png", "2.png").replace("textures", "etf/random");
        if (ETF_isExistingFile(new Identifier(checkPathETFFormat))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaPath, 3);
            keepGoing = true;
            successCount = 2;
        } else if (ETF_isExistingFile(new Identifier(checkPathOptifineFormat))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaPath, 0);
            keepGoing = true;
            successCount = 2;
        } else if (ETF_isExistingFile(new Identifier(checkPathOldRandomFormat))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaPath, 1);
            keepGoing = true;
            successCount = 2;
        } else if (ETF_isExistingFile(new Identifier(checkPath))) {
            ETF_PATH_OptifineOldVanillaETF_0123.put(vanillaPath, 2);
            keepGoing = true;
            successCount = 2;
        }
        //texture3.png and further optimized iterations
        int count = 2;
        while (keepGoing) {
            count++;

            checkPath = switch(ETF_PATH_OptifineOldVanillaETF_0123.get(vanillaPath)){
                case 3 -> vanillaPath.replace(".png", (count + ".png")).replace("textures", "etf/random");
                case 0 -> vanillaPath.replace(".png", (count + ".png")).replace("textures", "optifine/random");
                case 1 -> vanillaPath.replace(".png", (count + ".png")).replace("textures/entity", "optifine/mob");
                default-> vanillaPath.replace(".png", (count + ".png"));
            };

            keepGoing = ETF_isExistingFile(new Identifier(checkPath));
            if (keepGoing) successCount++;
        }
        //true if any random textures at all

        ETF_PATH_TotalTrueRandom.put(vanillaPath, successCount);
        ETF_PATH_OptifineOrTrueRandom.put(vanillaPath, false);

    }


    default void ETF_setEmissiveSuffix() {
        try {
            Properties suffix = ETF_readProperties("optifine/emissive.properties");
            if (suffix.isEmpty()) {
                suffix = ETF_readProperties("textures/emissive.properties");
            }
            Set<String> builder = new HashSet<>();
            if (suffix.contains("entities.suffix.emissive")) {
                builder.add(suffix.getProperty("entities.suffix.emissive"));
            }
            if (suffix.contains("suffix.emissive")) {
                builder.add(suffix.getProperty("suffix.emissive"));
            }
            if (ETFConfigData.alwaysCheckVanillaEmissiveSuffix) {
                builder.add("_e");
            }
            ETF_emissiveSuffixes = builder.toArray(new String[0]);
            if (ETF_emissiveSuffixes.length == 0) {
                ETF_modMessage("Error! Default emissive suffix '_e' used", false);
                ETF_emissiveSuffixes = new String[]{"_e"};
            }
        } catch (Exception e) {
            ETF_modMessage("Error! default emissive suffix '_e' used", false);
            ETF_emissiveSuffixes = new String[]{"_e"};
        }
    }

    default void ETF_saveConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!config.getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(gson.toJson(ETFConfigData));
            fileWriter.close();
        } catch (IOException e) {
            ETF_modMessage("Config could not be saved", false);
        }
    }

    default void ETF_checkPlayerForSkinFeatures(UUID id, PlayerEntity player) {
        //if on an enemy team option to disable skin features loading
        if (ETFConfigData.skinFeaturesEnabled
                && (ETFConfigData.enableEnemyTeamPlayersSkinFeatures
                || (player.isTeammate(MinecraftClient.getInstance().player)
                || player.getScoreboardTeam() == null))
        ) {
            // skip if tried to recently
            if (ETF_UUID_playerLastSkinCheck.containsKey(id)) {
                if (ETF_UUID_playerLastSkinCheck.get(id) + 6000 > System.currentTimeMillis()) {
                    return;
                }
            }
            ETF_UUID_playerSkinDownloadedYet.put(id, false);
            ETF_UUID_playerHasCape.put(id, ((AbstractClientPlayerEntity) player).canRenderCapeTexture());
            ETF_getSkin(player);
        }
    }


    private void ETF_getSkin(PlayerEntity player) {
        UUID id = player.getUuid();


        try {
            boolean hasCape = ((AbstractClientPlayerEntity) player).canRenderCapeTexture();
            String url = "";
            String capeurl = null;
            @SuppressWarnings("ConstantConditions")
            PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(id);
            @SuppressWarnings("ConstantConditions")
            GameProfile gameProfile = playerListEntry.getProfile();
            PropertyMap texturesMap = gameProfile.getProperties();
            Collection<Property> properties = texturesMap.get("textures");
            for (Property p :
                    properties) {
                JsonObject props = JsonParser.parseString(new String(Base64.getDecoder().decode((p.getValue())))).getAsJsonObject();
                url = ((JsonObject) ((JsonObject) props.get("textures")).get("SKIN")).get("url").getAsString();
                //le cape
                if (hasCape) {
                    try {
                        capeurl = ((JsonObject) ((JsonObject) props.get("textures")).get("CAPE")).get("url").getAsString();
                    } catch (Exception e) {
                        //modMessage("no cape",false);
                    }
                }

                break;
            }


                /*System.out.println(p.getValue());
{
  "timestamp" : 1645524822329,
  "profileId" : "fd22e573178c415a94fee476b328abfd",
  "profileName" : "Benjamin",
  "textures" : {
    "SKIN" : {
      "url" : "http://textures.minecraft.net/texture/a81cd0629057a42f3d8b7b714b1e233a3f89e33faeb67d3796a52df44619e888"
    },
    "CAPE" : {
      "url" : "http://textures.minecraft.net/texture/2340c0e03dd24a11b15a8b33c2a7e9e32abb2051b2481d0ba7defd635ca7a933"
    }
  }
}
                */

            ETF_downloadImageFromUrl(player, url, "VANILLA_SKIN", capeurl);
        } catch (Exception e) {
            ETF_skinFailed(id);
        }

    }

    private void ETF_downloadImageFromUrl(PlayerEntity player, String url, @SuppressWarnings("SameParameterValue") String sendFileToMethodKey) {
        ETF_downloadImageFromUrl(player, url, sendFileToMethodKey, null, false);
    }

    private void ETF_downloadImageFromUrl(PlayerEntity player, String url, @SuppressWarnings("SameParameterValue") String sendFileToMethodKey, String url2) {
        ETF_downloadImageFromUrl(player, url, sendFileToMethodKey, url2, false);
    }

    private void ETF_downloadImageFromUrl(PlayerEntity player, String url, String sendFileToMethodKey, @Nullable String url2, @SuppressWarnings("SameParameterValue") boolean isFile) {
        try {
            //required for vanilla cape
            boolean do2urls = url2 != null;
            CompletableFuture.runAsync(() -> {
                HttpURLConnection httpURLConnection = null;
                HttpURLConnection httpURLConnection2 = null;
                try {
                    httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(false);
                    httpURLConnection.connect();
                    if (do2urls) {
                        httpURLConnection2 = (HttpURLConnection) (new URL(url2)).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                        httpURLConnection2.setDoInput(true);
                        httpURLConnection2.setDoOutput(false);
                        httpURLConnection2.connect();
                    }
                    boolean newHas2 = do2urls && httpURLConnection2.getResponseCode() / 100 == 2;
                    if (httpURLConnection.getResponseCode() / 100 == 2) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        InputStream inputStreamCape = null;
                        if (newHas2) {
                            inputStreamCape = httpURLConnection2.getInputStream();
                        }
                        InputStream finalInputStreamCape = inputStreamCape;
                        MinecraftClient.getInstance().execute(() -> {
                            if (isFile) {
                                String read;
                                try {
                                    read = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                                } catch (Exception e) {
                                    read = null;
                                }
                                ETF_directFileFromUrlToMethod(read, sendFileToMethodKey);
                            } else {
                                NativeImage one = this.ETF_loadTexture(inputStream);
                                NativeImage two = null;
                                if (newHas2) {
                                    two = this.ETF_loadTexture(finalInputStreamCape);
                                }
                                if (one != null) {
                                    ETF_directImageFromUrlToMethod(player, one, sendFileToMethodKey, two);
                                } else {
                                    //modMessage("downloading image failed", false);
                                    ETF_skinFailed(player.getUuid());
                                }
                            }
                            if (ETF_URL_HTTPtoDisconnect1.containsKey(url)) {
                                if (ETF_URL_HTTPtoDisconnect1.get(url) != null) {
                                    ETF_URL_HTTPtoDisconnect1.get(url).disconnect();
                                }
                                ETF_URL_HTTPtoDisconnect1.remove(url);
                            }
                            if (ETF_URL_HTTPtoDisconnect2.containsKey(url2)) {
                                if (ETF_URL_HTTPtoDisconnect2.get(url2) != null) {
                                    ETF_URL_HTTPtoDisconnect2.get(url2).disconnect();
                                }
                                ETF_URL_HTTPtoDisconnect2.remove(url2);
                            }
                        });

                    }
                } catch (Exception var6) {
                    ETF_URL_HTTPtoDisconnect1.put(url, httpURLConnection);
                    ETF_URL_HTTPtoDisconnect2.put(url2, httpURLConnection2);
                } finally {
                    ETF_URL_HTTPtoDisconnect1.put(url, httpURLConnection);
                    ETF_URL_HTTPtoDisconnect2.put(url2, httpURLConnection2);
                }

            }, Util.getMainWorkerExecutor());

        } catch (Exception e) {
            //
        }
    }

    private void ETF_directFileFromUrlToMethod(String fileString, String sendFileToMethodKey) {
        //switch
        if (fileString != null) {
            if (sendFileToMethodKey.equals("ETF_CAPE")) {
                System.out.println(fileString);
            }
        }

    }

    private void ETF_skinFailed(UUID id) {
        ETF_UUID_playerLastSkinCheck.put(id, System.currentTimeMillis());
        if (!ETF_UUID_playerLastSkinCheckCount.containsKey(id)) {
            ETF_UUID_playerLastSkinCheckCount.put(id, 0);
        } else {
            ETF_UUID_playerLastSkinCheckCount.put(id, ETF_UUID_playerLastSkinCheckCount.get(id) + 1);
        }

        //modMessage("Player skin {" + name + "} unavailable for feature check. try number "+UUID_playerLastSkinCheckCount.get(id)+". Reason failed = "+(reason+1), false);
        ///give up after a few checks
        if (ETF_UUID_playerLastSkinCheckCount.get(id) > 5) {
            ETF_UUID_playerHasFeatures.put(id, false);
        }
        ETF_UUID_playerSkinDownloadedYet.remove(id);
    }

    private void ETF_directImageFromUrlToMethod(PlayerEntity player, NativeImage image, String sendFileToMethodKey, @Nullable NativeImage image2) {
        //switch
        UUID id = player.getUuid();
        if (sendFileToMethodKey.equals("VANILLA_SKIN")) {
            if (image != null) {
                ETF_skinLoaded(player, image, image2);
            } else {
                //modMessage("Player skin {" + player.getName().getString() + "} unavailable for feature check", false);
                ETF_skinFailed(id);
            }
        } else if (sendFileToMethodKey.equals("THIRD_PARTY_CAPE")) {
            if (image != null) {
                //optifine resizes them for space cause expensive servers I guess
                if (image.getWidth() % image.getHeight() != 0) {
                    ETF_registerNativeImageToIdentifier(ETF_resizeOptifineImage(image), ETF_SKIN_NAMESPACE + id + "_cape.png");
                } else {
                    ETF_registerNativeImageToIdentifier(image, ETF_SKIN_NAMESPACE + id + "_cape.png");
                }
                ETF_UUID_playerHasCustomCape.put(id, true);
            } else {
                ETF_modMessage("Player skin {" + player.getName().getString() + "} no THIRD_PARTY_CAPE Found", false);
                //registerNativeImageToIdentifier(getNativeImageFromID(new Identifier("etf:capes/error.png")), SKIN_NAMESPACE + id + "_cape.png");
                ETF_UUID_playerHasCustomCape.put(id, false);
            }

        }
    }

    private NativeImage ETF_resizeOptifineImage(NativeImage image) {
        int newWidth = 64;
        while (newWidth < image.getWidth()) {
            newWidth = newWidth + newWidth;
        }
        int newHeight = newWidth / 2;
        NativeImage resizedImage = ETF_emptyNativeImage(newWidth, newHeight);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                resizedImage.setColor(x, y, image.getColor(x, y));
            }
        }
        return resizedImage;
    }

    private NativeImage ETF_loadTexture(InputStream stream) {
        NativeImage nativeImage = null;

        try {
            nativeImage = NativeImage.read(stream);

        } catch (Exception var4) {
            //modMessage("failed 165165651" + var4, false);
        }

        return nativeImage;
    }


    private int ETF_getSkinPixelColourToNumber(int color) {
        //            pink   cyan     red       green      brown    blue     orange     yellow
        //colours = -65281, -256, -16776961, -16711936, -16760705, -65536, -16744449, -14483457
        return switch (color) {
            case -65281 -> 1;
            case -256 -> 2;
            case -16776961 -> 3;
            case -16711936 -> 4;
            case -16760705 -> 5;
            case -65536 -> 6;
            case -16744449 -> 7;
            case -14483457 -> 8;
            case -12362096 -> 666; //villager nose color
            default -> 0;
        };
    }

    private void ETF_skinLoaded(PlayerEntity player, NativeImage skin, @Nullable NativeImage cape) {
        UUID id = player.getUuid();
        if (skin != null) {
            if (skin.getColor(1, 16) == -16776961 &&
                    skin.getColor(0, 16) == -16777089 &&
                    skin.getColor(0, 17) == -16776961 &&
                    skin.getColor(2, 16) == -16711936 &&
                    skin.getColor(3, 16) == -16744704 &&
                    skin.getColor(3, 17) == -16711936 &&
                    skin.getColor(0, 18) == -65536 &&
                    skin.getColor(0, 19) == -8454144 &&
                    skin.getColor(1, 19) == -65536 &&
                    skin.getColor(3, 18) == -1 &&
                    skin.getColor(2, 19) == -1 &&
                    skin.getColor(3, 18) == -1
            ) {
                //this has texture features
                ETF_modMessage("Found Player {" + id + "} with texture features in skin.", false);
                ETF_UUID_playerHasFeatures.put(id, true);
                //find what features
                //pink = -65281, blue = -256
                //            pink   cyan     red       green      brown    blue     orange     yellow
                //colours = -65281, -256, -16776961, -16711936, -16760705, -65536, -16744449, -14483457

                //check Choice Box
                int[] choiceBoxChoices = {ETF_getSkinPixelColourToNumber(skin.getColor(52, 16)),
                        ETF_getSkinPixelColourToNumber(skin.getColor(52, 17)),
                        ETF_getSkinPixelColourToNumber(skin.getColor(52, 18)),
                        ETF_getSkinPixelColourToNumber(skin.getColor(52, 19)),
                        ETF_getSkinPixelColourToNumber(skin.getColor(53, 16)),
                        ETF_getSkinPixelColourToNumber(skin.getColor(53, 17))};

                //villager nose check
                boolean noseUpper = (ETF_getSkinPixelColourToNumber(skin.getColor(43, 13)) == 666 && ETF_getSkinPixelColourToNumber(skin.getColor(44, 13)) == 666 &&
                        ETF_getSkinPixelColourToNumber(skin.getColor(43, 14)) == 666 && ETF_getSkinPixelColourToNumber(skin.getColor(44, 14)) == 666 &&
                        ETF_getSkinPixelColourToNumber(skin.getColor(43, 15)) == 666 && ETF_getSkinPixelColourToNumber(skin.getColor(44, 15)) == 666);
                boolean noseLower = (ETF_getSkinPixelColourToNumber(skin.getColor(11, 13)) == 666 && ETF_getSkinPixelColourToNumber(skin.getColor(12, 13)) == 666 &&
                        ETF_getSkinPixelColourToNumber(skin.getColor(11, 14)) == 666 && ETF_getSkinPixelColourToNumber(skin.getColor(12, 14)) == 666 &&
                        ETF_getSkinPixelColourToNumber(skin.getColor(11, 15)) == 666 && ETF_getSkinPixelColourToNumber(skin.getColor(12, 15)) == 666);
                if (noseUpper) {
                    ETF_deletePixels(skin, 43, 13, 44, 15);
                }
                ETF_UUID_playerHasVillagerNose.put(id, noseLower || noseUpper);

                //check for coat bottom
                //pink to copy coat    light blue to remove from legs
                NativeImage coatSkin = null;
                int controllerCoat = choiceBoxChoices[1];
                if (controllerCoat >= 1 && controllerCoat <= 8) {
                    int lengthOfCoat = choiceBoxChoices[2] - 1;
                    Identifier coatID = new Identifier(ETF_SKIN_NAMESPACE + id + "_coat.png");
                    coatSkin = ETF_getOrRemoveCoatTexture(skin, lengthOfCoat, controllerCoat >= 5);
                    ETF_registerNativeImageToIdentifier(coatSkin, coatID.toString());
                    ETF_UUID_playerHasCoat.put(id, true);
                    if (controllerCoat == 2 || controllerCoat == 4 || controllerCoat == 6 || controllerCoat == 8) {
                        //delete original pixel from skin
                        ETF_deletePixels(skin, 4, 32, 7, 35);
                        ETF_deletePixels(skin, 4, 48, 7, 51);
                        ETF_deletePixels(skin, 0, 36, 15, 36 + lengthOfCoat);
                        ETF_deletePixels(skin, 0, 52, 15, 52 + lengthOfCoat);
                    }
                    //red or green make fat coat
                    ETF_UUID_playerHasFatCoat.put(id, controllerCoat == 3 || controllerCoat == 4 || controllerCoat == 7 || controllerCoat == 8);


                } else {
                    ETF_UUID_playerHasCoat.put(id, false);
                }
                //check for transparency options
                //System.out.println("about to check");
                if (ETFConfigData.skinFeaturesEnableTransparency) {
                    if (ETF_canTransparentSkin(skin)) {
                        Identifier transId = new Identifier(ETF_SKIN_NAMESPACE + id + "_transparent.png");
                        ETF_UUID_playerTransparentSkinId.put(id, transId);
                        ETF_registerNativeImageToIdentifier(skin, transId.toString());

                    } else {
                        ETF_modMessage("Skin was too transparent or had other problems", false);
                    }
                }

                //blink
                NativeImage blinkSkinFile = null;
                NativeImage blinkSkinFile2 = null;
                int blinkChoice = choiceBoxChoices[0];
                if (blinkChoice >= 1 && blinkChoice <= 5) {
                    //check if lazy blink
                    ETF_PATH_HasBlink.put(ETF_SKIN_NAMESPACE + id + ".png", true);
                    if (blinkChoice <= 2) {
                        //blink 1 frame if either pink or blue optional
                        blinkSkinFile = ETF_returnOptimizedBlinkFace(skin, ETF_getSkinPixelBounds("face1"), 1, ETF_getSkinPixelBounds("face3"));

                        ETF_registerNativeImageToIdentifier(blinkSkinFile, ETF_SKIN_NAMESPACE + id + "_blink.png");

                        //blink is 2 frames with blue optional
                        if (blinkChoice == 2) {
                            blinkSkinFile2 = ETF_returnOptimizedBlinkFace(skin, ETF_getSkinPixelBounds("face2"), 1, ETF_getSkinPixelBounds("face4"));
                            ETF_PATH_HasBlink2.put(ETF_SKIN_NAMESPACE + id + ".png", true);
                            ETF_registerNativeImageToIdentifier(blinkSkinFile2, ETF_SKIN_NAMESPACE + id + "_blink2.png");
                        } else {
                            ETF_PATH_HasBlink2.put(ETF_SKIN_NAMESPACE + id + ".png", false);
                        }
                    } else {//optimized blink
                        int eyeHeightTopDown = choiceBoxChoices[3];
                        if (eyeHeightTopDown > 8 || eyeHeightTopDown < 1) {
                            eyeHeightTopDown = 1;
                        }
                        //optimized 1p high eyes
                        if (blinkChoice == 3) {
                            blinkSkinFile = ETF_returnOptimizedBlinkFace(skin, ETF_getSkinPixelBounds("optimizedEyeSmall"), eyeHeightTopDown);

                            ETF_registerNativeImageToIdentifier(blinkSkinFile, ETF_SKIN_NAMESPACE + id + "_blink.png");

                        } else if (blinkChoice == 4) {
                            blinkSkinFile = ETF_returnOptimizedBlinkFace(skin, ETF_getSkinPixelBounds("optimizedEye2High"), eyeHeightTopDown);
                            blinkSkinFile2 = ETF_returnOptimizedBlinkFace(skin, ETF_getSkinPixelBounds("optimizedEye2High_second"), eyeHeightTopDown);
                            ETF_PATH_HasBlink2.put(ETF_SKIN_NAMESPACE + id + ".png", true);

                            ETF_registerNativeImageToIdentifier(blinkSkinFile, ETF_SKIN_NAMESPACE + id + "_blink.png");
                            ETF_registerNativeImageToIdentifier(blinkSkinFile2, ETF_SKIN_NAMESPACE + id + "_blink2.png");
                        } else /*if( blinkChoice == 5)*/ {
                            blinkSkinFile = ETF_returnOptimizedBlinkFace(skin, ETF_getSkinPixelBounds("optimizedEye4High"), eyeHeightTopDown);
                            blinkSkinFile2 = ETF_returnOptimizedBlinkFace(skin, ETF_getSkinPixelBounds("optimizedEye4High_second"), eyeHeightTopDown);
                            ETF_PATH_HasBlink2.put(ETF_SKIN_NAMESPACE + id + ".png", true);
                            ETF_registerNativeImageToIdentifier(blinkSkinFile, ETF_SKIN_NAMESPACE + id + "_blink.png");
                            ETF_registerNativeImageToIdentifier(blinkSkinFile2, ETF_SKIN_NAMESPACE + id + "_blink2.png");
                        }
                    }


                }
                if (!ETF_PATH_HasBlink.containsKey(ETF_SKIN_NAMESPACE + id + ".png")) {
                    ETF_PATH_HasBlink.put(ETF_SKIN_NAMESPACE + id + ".png", false);
                }
                if (!ETF_PATH_HasBlink2.containsKey(ETF_SKIN_NAMESPACE + id + ".png")) {
                    ETF_PATH_HasBlink2.put(ETF_SKIN_NAMESPACE + id + ".png", false);
                }

                //check for cape recolor
                int capeChoice1 = choiceBoxChoices[4];
                // custom cape data experiment
                // https://drive.google.com/uc?export=download&id=1rn1swLadqdMiLirz9Nrae0_VHFrTaJQe
                //downloadImageFromUrl(player, "https://drive.google.com/uc?export=download&id=1rn1swLadqdMiLirz9Nrae0_VHFrTaJQe", "ETF_CAPE",null,true);
                if ((capeChoice1 >= 1 && capeChoice1 <= 3) || capeChoice1 == 666) {
                    switch (capeChoice1) {
                        case 1 -> //custom in skin
                                cape = ETF_returnCustomTexturedCape(skin);
                        case 2 -> {
                            cape = null;
                            // minecraft capes mod
                            //https://minecraftcapes.net/profile/fd22e573178c415a94fee476b328abfd/cape/
                            ETF_downloadImageFromUrl(player, "https://minecraftcapes.net/profile/" + player.getUuidAsString().replace("-", "") + "/cape/", "THIRD_PARTY_CAPE");
                        }
                        case 3 -> {
                            cape = null;
                            //  https://optifine.net/capes/Benjamin.png
                            ETF_downloadImageFromUrl(player, "https://optifine.net/capes/" + player.getName().getString() + ".png", "THIRD_PARTY_CAPE");
                        }
                        case 666 -> cape = ETF_getNativeImageFromID(new Identifier("etf:capes/error.png"));
                        default -> {
                            //cape = getNativeImageFromID(new Identifier("etf:capes/blank.png"));
                        }
                    }
                }
                if (cape != null) {
                    if ((capeChoice1 >= 1 && capeChoice1 <= 3) || capeChoice1 == 666) {//custom chosen
                        ETF_registerNativeImageToIdentifier(cape, ETF_SKIN_NAMESPACE + id + "_cape.png");
                        ETF_UUID_playerHasCustomCape.put(id, true);
                    }
                }
                if (!ETF_UUID_playerHasCustomCape.containsKey(id)) {
                    ETF_UUID_playerHasCustomCape.put(id, false);
                }


                //check for marker choices
                //  1 = Emissives,  2 = Enchanted
                List<Integer> markerChoices = List.of(ETF_getSkinPixelColourToNumber(skin.getColor(1, 17)),
                        ETF_getSkinPixelColourToNumber(skin.getColor(1, 18)),
                        ETF_getSkinPixelColourToNumber(skin.getColor(2, 17)),
                        ETF_getSkinPixelColourToNumber(skin.getColor(2, 18)));

                //enchanted
                ETF_UUID_playerHasEnchant.put(id, markerChoices.contains(2));
                if (markerChoices.contains(2)) {
                    int[] boxChosenBounds = ETF_getSkinPixelBounds("marker" + (markerChoices.indexOf(2) + 1));
                    NativeImage check = ETF_returnMatchPixels(skin, boxChosenBounds);
                    if (check != null) {
                        ETF_registerNativeImageToIdentifier(check, ETF_SKIN_NAMESPACE + id + "_enchant.png");
                        if (blinkSkinFile != null) {
                            NativeImage checkBlink = ETF_returnMatchPixels(blinkSkinFile, boxChosenBounds);
                            ETF_registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, this::ETF_emptyNativeImage), ETF_SKIN_NAMESPACE + id + "_blink_enchant.png");
                        }
                        if (blinkSkinFile2 != null) {
                            NativeImage checkBlink = ETF_returnMatchPixels(blinkSkinFile2, boxChosenBounds);
                            ETF_registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, this::ETF_emptyNativeImage), ETF_SKIN_NAMESPACE + id + "_blink2_enchant.png");
                        }
                        if (coatSkin != null) {
                            NativeImage checkCoat = ETF_returnMatchPixels(coatSkin, boxChosenBounds);
                            ETF_registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCoat, this::ETF_emptyNativeImage), ETF_SKIN_NAMESPACE + id + "_coat_enchant.png");
                        }

                        // NativeImage checkCape = returnMatchPixels(skin, boxChosenBounds,cape);
                        // registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCape, this::emptyNativeImage), SKIN_NAMESPACE + id + "_cape_enchant.png");

                    } else {
                        ETF_UUID_playerHasEnchant.put(id, false);
                    }

                }
                //emissives
                ETF_UUID_playerHasEmissive.put(id, markerChoices.contains(1));
                if (markerChoices.contains(1)) {
                    int[] boxChosenBounds = ETF_getSkinPixelBounds("marker" + (markerChoices.indexOf(1) + 1));
                    NativeImage check = ETF_returnMatchPixels(skin, boxChosenBounds);
                    if (check != null) {
                        ETF_registerNativeImageToIdentifier(check, ETF_SKIN_NAMESPACE + id + "_e.png");
                        if (blinkSkinFile != null) {
                            NativeImage checkBlink = ETF_returnMatchPixels(blinkSkinFile, boxChosenBounds);
                            ETF_registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, this::ETF_emptyNativeImage), ETF_SKIN_NAMESPACE + id + "_blink_e.png");
                        }
                        if (blinkSkinFile2 != null) {
                            NativeImage checkBlink = ETF_returnMatchPixels(blinkSkinFile2, boxChosenBounds);
                            ETF_registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, this::ETF_emptyNativeImage), ETF_SKIN_NAMESPACE + id + "_blink2_e.png");
                        }
                        if (coatSkin != null) {
                            NativeImage checkCoat = ETF_returnMatchPixels(coatSkin, boxChosenBounds);
                            ETF_registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCoat, this::ETF_emptyNativeImage), ETF_SKIN_NAMESPACE + id + "_coat_e.png");
                        }

                        //  NativeImage checkCape = returnMatchPixels(skin, boxChosenBounds,cape);
                        // registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCape, this::emptyNativeImage), SKIN_NAMESPACE + id + "_cape_e.png");

                    } else {
                        ETF_UUID_playerHasEmissive.put(id, false);
                    }

                }

            } else {
                ETF_UUID_playerHasFeatures.put(id, false);
                // System.out.println("worked but no features");
            }
        } else { //http failed
            //UUID_playerHasFeatures.put(id, false);
            ETF_skinFailed(id);
        }
        ETF_UUID_playerSkinDownloadedYet.put(id, true);
    }

    private NativeImage ETF_emptyNativeImage() {
        return ETF_emptyNativeImage(64, 64);
    }

    private NativeImage ETF_emptyNativeImage(int Width, int Height) {
        NativeImage empty = new NativeImage(Width, Height, false);
        empty.fillRect(0, 0, Width, Height, 0);
        return empty;
    }

    private int[] ETF_getSkinPixelBounds(String choiceKey) {
        return switch (choiceKey) {
            case "marker1" -> new int[]{56, 16, 63, 23};
            case "marker2" -> new int[]{56, 24, 63, 31};
            case "marker3" -> new int[]{56, 32, 63, 39};
            case "marker4" -> new int[]{56, 40, 63, 47};
            case "optimizedEyeSmall" -> new int[]{12, 16, 19, 16};
            case "optimizedEye2High" -> new int[]{12, 16, 19, 17};
            case "optimizedEye2High_second" -> new int[]{12, 18, 19, 19};
            case "optimizedEye4High" -> new int[]{12, 16, 19, 19};
            case "optimizedEye4High_second" -> new int[]{36, 16, 43, 19};
            case "face1" -> new int[]{0, 0, 7, 7};
            case "face2" -> new int[]{24, 0, 31, 7};
            case "face3" -> new int[]{32, 0, 39, 7};
            case "face4" -> new int[]{56, 0, 63, 7};
            case "cape1" -> new int[]{12, 32, 19, 35};
            case "cape2" -> new int[]{36, 32, 43, 35};
            case "cape3" -> new int[]{12, 48, 19, 51};
            case "cape4" -> new int[]{28, 48, 35, 51};
            case "cape5.1" -> new int[]{44, 48, 45, 51};
            case "cape5.2" -> new int[]{46, 48, 47, 51};
            case "cape5.3" -> new int[]{48, 48, 49, 51};
            case "cape5.4" -> new int[]{50, 48, 51, 51};
            case "capeVertL" -> new int[]{1, 1, 1, 16};
            case "capeVertR" -> new int[]{10, 1, 10, 16};
            case "capeHorizL" -> new int[]{1, 1, 10, 1};
            case "capeHorizR" -> new int[]{1, 16, 10, 16};
            default -> new int[]{0, 0, 0, 0};
        };
    }


    private NativeImage ETF_returnCustomTexturedCape(NativeImage skin) {
        NativeImage cape = ETF_emptyNativeImage(64, 32);
        NativeImage elytra = ETF_getNativeImageFromID(new Identifier("etf:capes/default_elytra.png"));
        if (elytra == null) {
            elytra = ETF_getNativeImageFromID(new Identifier("textures/entity/elytra.png"));
        }//not else
        if (elytra != null) {
            cape.copyFrom(elytra);
        }
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape1"), 1, 1);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape1"), 12, 1);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape2"), 1, 5);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape2"), 12, 5);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape3"), 1, 9);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape3"), 12, 9);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape4"), 1, 13);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape4"), 12, 13);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape5.1"), 9, 1);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape5.1"), 20, 1);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape5.2"), 9, 5);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape5.2"), 20, 5);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape5.3"), 9, 9);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape5.3"), 20, 9);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape5.4"), 9, 13);
        ETF_copyToPixels(skin, cape, ETF_getSkinPixelBounds("cape5.4"), 20, 13);

        ETF_copyToPixels(cape, cape, ETF_getSkinPixelBounds("capeVertL"), 0, 1);
        ETF_copyToPixels(cape, cape, ETF_getSkinPixelBounds("capeVertR"), 11, 1);
        ETF_copyToPixels(cape, cape, ETF_getSkinPixelBounds("capeHorizL"), 1, 0);
        ETF_copyToPixels(cape, cape, ETF_getSkinPixelBounds("capeHorizR"), 11, 0);

        return cape;
    }

    private NativeImage ETF_getOrRemoveCoatTexture(NativeImage skin, int lengthOfCoat, boolean ignoreTopTexture) {

        NativeImage coat = new NativeImage(64, 64, false);
        coat.fillRect(0, 0, 64, 64, 0);

        //top
        if (!ignoreTopTexture) {
            ETF_copyToPixels(skin, coat, 4, 32, 7, 35 + lengthOfCoat, 20, 32);
            ETF_copyToPixels(skin, coat, 4, 48, 7, 51 + lengthOfCoat, 24, 32);
        }
        //sides
        ETF_copyToPixels(skin, coat, 0, 36, 7, 36 + lengthOfCoat, 16, 36);
        ETF_copyToPixels(skin, coat, 12, 36, 15, 36 + lengthOfCoat, 36, 36);
        ETF_copyToPixels(skin, coat, 4, 52, 15, 52 + lengthOfCoat, 24, 36);
        //ENCHANT AND EMISSIVES
        ETF_copyToPixels(skin, coat, 56, 16, 63, 47, 0, 0);
        return coat;

    }

    // modifiers are distance from x1,y1 to copy
    private void ETF_copyToPixels(NativeImage source, NativeImage dest, int[] bounds, int copyToX, int CopyToY) {
        ETF_copyToPixels(source, dest, bounds[0], bounds[1], bounds[2], bounds[3], copyToX, CopyToY);
    }

    private void ETF_copyToPixels(NativeImage source, NativeImage dest, int x1, int y1, int x2, int y2, int copyToX, int copyToY) {
        int copyToXRelative = copyToX - x1;
        int copyToYRelative = copyToY - y1;
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                dest.setColor(x + copyToXRelative, y + copyToYRelative, source.getColor(x, y));
            }
        }
    }

    private void ETF_deletePixels(NativeImage source, int x1, int y1, int x2, int y2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                source.setColor(x, y, 0);
            }
        }
    }

    private void ETF_registerNativeImageToIdentifier(NativeImage img, String identifierPath) {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        NativeImageBackedTexture bob = new NativeImageBackedTexture(img);
        textureManager.registerTexture(new Identifier(identifierPath), bob);

    }

    private int ETF_countTransparentInBox(NativeImage img, int x1, int y1, int x2, int y2) {
        int counter = 0;
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                //ranges from  0 to 127  then wraps around negatively -127 to -1  totalling 0 to 255
                int i = img.getOpacity(x, y);
                if (i < 0) {
                    i += 256;
                }
                //adjusted to 0 to 256
                counter += i;

            }
        }
        return counter;
    }

    private boolean ETF_canTransparentSkin(NativeImage skin) {
        if (ETFConfigData.skinFeaturesEnableFullTransparency) {
            return true;
        } else {
            int countTransparent = 0;
            //map of bottom skin layer in cubes
            countTransparent += ETF_countTransparentInBox(skin, 8, 0, 23, 15);
            countTransparent += ETF_countTransparentInBox(skin, 0, 20, 55, 31);
            countTransparent += ETF_countTransparentInBox(skin, 0, 8, 7, 15);
            countTransparent += ETF_countTransparentInBox(skin, 24, 8, 31, 15);
            countTransparent += ETF_countTransparentInBox(skin, 0, 16, 11, 19);
            countTransparent += ETF_countTransparentInBox(skin, 20, 16, 35, 19);
            countTransparent += ETF_countTransparentInBox(skin, 44, 16, 51, 19);
            countTransparent += ETF_countTransparentInBox(skin, 20, 48, 27, 51);
            countTransparent += ETF_countTransparentInBox(skin, 36, 48, 43, 51);
            countTransparent += ETF_countTransparentInBox(skin, 16, 52, 47, 63);
            //do not allow skins under 40% ish total opacity
            //1648 is total pixels that are not allowed transparent by vanilla
            int average = (countTransparent / 1648); // should be 0 to 256
            //System.out.println("average ="+average);
            return average >= 100;
        }
    }

    private NativeImage ETF_returnOptimizedBlinkFace(NativeImage baseSkin, int[] eyeBounds, int eyeHeightFromTopDown) {
        return ETF_returnOptimizedBlinkFace(baseSkin, eyeBounds, eyeHeightFromTopDown, null);
    }

    private NativeImage ETF_returnOptimizedBlinkFace(NativeImage baseSkin, int[] eyeBounds, int eyeHeightFromTopDown, int[] secondLayerBounds) {
        NativeImage texture = new NativeImage(64, 64, false);
        texture.copyFrom(baseSkin);
        //copy face
        ETF_copyToPixels(baseSkin, texture, eyeBounds, 8, 8 + (eyeHeightFromTopDown - 1));
        //copy face overlay
        if (secondLayerBounds != null) {
            ETF_copyToPixels(baseSkin, texture, secondLayerBounds, 40, 8 + (eyeHeightFromTopDown - 1));
        }
        return texture;
    }

    @Nullable
    private NativeImage ETF_returnMatchPixels(NativeImage baseSkin, int[] boundsToCheck) {
        return ETF_returnMatchPixels(baseSkin, boundsToCheck, null);
    }

    @Nullable
    private NativeImage ETF_returnMatchPixels(NativeImage baseSkin, int[] boundsToCheck, @SuppressWarnings("SameParameterValue") @Nullable NativeImage second) {
        if (baseSkin == null) return null;

        boolean secondImage = second != null;
        Set<Integer> matchColors = new HashSet<>();
        for (int x = boundsToCheck[0]; x <= boundsToCheck[2]; x++) {
            for (int y = boundsToCheck[1]; y <= boundsToCheck[3]; y++) {
                if (baseSkin.getOpacity(x, y) != 0) {
                    matchColors.add(baseSkin.getColor(x, y));
                }
            }
        }
        if (matchColors.size() == 0) {
            return null;
        } else {
            NativeImage texture = !secondImage ? new NativeImage(64, 64, false) : new NativeImage(64, 32, false);
            if (!secondImage) {
                texture.copyFrom(baseSkin);
            } else {
                texture.copyFrom(second);
            }
            for (int x = 0; x < texture.getWidth(); x++) {
                for (int y = 0; y < texture.getHeight(); y++) {
                    if (!matchColors.contains(baseSkin.getColor(x, y))) {
                        texture.setColor(x, y, 0);
                    }
                }
            }
            return texture;
        }

    }

    default Identifier ETF_returnBlinkIdOrGiven(LivingEntity entity, String givenTexturePath, UUID id) {
        return ETF_returnBlinkIdOrGiven(entity, givenTexturePath, id, false);
    }


    default Identifier ETF_returnBlinkIdOrGiven(LivingEntity entity, String givenTexturePath, UUID id, boolean isPlayer) {
        if (ETFConfigData.enableBlinking) {
            if (!ETF_PATH_HasBlink.containsKey(givenTexturePath)) {
                //check for blink textures
                ETF_PATH_HasBlink.put(givenTexturePath, ETF_isExistingFileAndSameOrHigherResourcepackAs(new Identifier(givenTexturePath.replace(".png", "_blink.png")), new Identifier(givenTexturePath)));
                ETF_PATH_HasBlink2.put(givenTexturePath, ETF_isExistingFileAndSameOrHigherResourcepackAs(new Identifier(givenTexturePath.replace(".png", "_blink2.png")), new Identifier(givenTexturePath)));
                ETF_PATH_BlinkProps.put(givenTexturePath, ETF_readProperties(givenTexturePath.replace(".png", "_blink.properties"), givenTexturePath));

            }
            ETF_PATH_BlinkProps.putIfAbsent(givenTexturePath, null);
            ETF_PATH_HasBlink.putIfAbsent(givenTexturePath, false);
            ETF_PATH_HasBlink2.putIfAbsent(givenTexturePath, false);
            if (ETF_PATH_HasBlink.containsKey(givenTexturePath)) {
                if (ETF_PATH_HasBlink.get(givenTexturePath)) {
                    if (entity.getPose() == EntityPose.SLEEPING) {
                        return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                    }
                    //force eyes closed if blinded
                    else if (entity.hasStatusEffect(StatusEffects.BLINDNESS)) {
                        if (ETF_PATH_HasBlink2.containsKey(givenTexturePath)) {
                            return new Identifier(givenTexturePath.replace(".png", (ETF_PATH_HasBlink2.get(givenTexturePath) ? "_blink2.png" : "_blink.png")));
                        } else {
                            return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                        }
                    } else {
                        //do regular blinking
                        Properties props = ETF_PATH_BlinkProps.get(givenTexturePath);
                        int blinkLength;
                        int blinkFrequency;
                        if (props != null) {
                            blinkLength = props.containsKey("blinkLength") ?
                                    Integer.parseInt(props.getProperty("blinkLength").replaceAll("[^0-9]", "")) :
                                    ETFConfigData.blinkLength;
                            blinkFrequency = props.containsKey("blinkFrequency") ?
                                    Integer.parseInt(props.getProperty("blinkFrequency").replaceAll("[^0-9]", "")) :
                                    ETFConfigData.blinkFrequency;
                        } else {
                            blinkLength = ETFConfigData.blinkLength;
                            blinkFrequency = ETFConfigData.blinkFrequency;
                        }


                        // long timer = entity.world.getTime() % blinkFrequency;
                        //int blinkTimeVariedByUUID = Math.abs(id.hashCode()) % blinkFrequency;
                        //make blink timer not overlap the wrap around to 0
                        //if (blinkTimeVariedByUUID < blinkLength) blinkTimeVariedByUUID = blinkLength;
                        //if (blinkTimeVariedByUUID > blinkFrequency - blinkLength)
                        //  blinkTimeVariedByUUID = blinkFrequency - blinkLength;

                        if (!ETF_UUID_NextBlinkTime.containsKey(id)) {
                            ETF_UUID_NextBlinkTime.put(id, entity.world.getTime() + blinkLength + 1);
                        }
                        long nextBlink = ETF_UUID_NextBlinkTime.get(id);
                        long currentTime = entity.world.getTime();

                        if (currentTime >= nextBlink - blinkLength && currentTime <= nextBlink + blinkLength) {
                            if (ETF_PATH_HasBlink2.containsKey(givenTexturePath)) {
                                if (ETF_PATH_HasBlink2.get(givenTexturePath)) {
                                    if (currentTime >= nextBlink - (blinkLength / 3) && currentTime <= nextBlink + (blinkLength / 3)) {
                                        return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                                    }
                                    return new Identifier(givenTexturePath.replace(".png", "_blink2.png"));
                                }
                            }
                            if (!(currentTime > nextBlink)) {
                                return new Identifier(givenTexturePath.replace(".png", "_blink.png"));
                            }
                        } else if (currentTime > nextBlink + blinkLength) {
                            //calculate new next blink
                            ETF_UUID_NextBlinkTime.put(id, currentTime + entity.getRandom().nextInt(blinkFrequency) + 20);
                        }
                    }
                }
            }
        }

        if (isPlayer && ETFConfigData.skinFeaturesEnabled
                && ETF_UUID_playerTransparentSkinId.containsKey(id) && (ETFConfigData.enableEnemyTeamPlayersSkinFeatures
                || (entity.isTeammate(MinecraftClient.getInstance().player) || entity.getScoreboardTeam() == null))) {
            Identifier ident = ETF_UUID_playerTransparentSkinId.get(id);
            if (ident != null) {
                return ident;
            }
        }
        return new Identifier(givenTexturePath);
    }

    //assume random texture is fully calculated and applied already for UUID
    //no update logic as that will be kept to living entity renderer to reset only once per UUID
    default Identifier ETF_GeneralReturnAlteredTexture(Identifier texture, Entity entity) {
        if (entity == null) return texture;
        UUID id = entity.getUuid();
        if (ETFConfigData.enableCustomTextures) {
            if (ETF_UUID_randomTextureSuffix.containsKey(id)) {
                if (ETF_UUID_randomTextureSuffix.get(id) != 0) {
                    return ETF_returnBlinkIdOrGiven((LivingEntity) entity, ETF_returnOptifineOrVanillaIdentifier(texture.toString(), ETF_UUID_randomTextureSuffix.get(id)).toString(), id);
                } else {
                    if (!ETF_PATH_HasOptifineDefaultReplacement.containsKey(texture.toString())) {
                        ETF_PATH_HasOptifineDefaultReplacement.put(texture.toString(), ETF_isExistingFile(ETF_returnOptifineOrVanillaIdentifier(texture.toString())));
                    }
                    if (ETF_PATH_HasOptifineDefaultReplacement.get(texture.toString())) {
                        return ETF_returnBlinkIdOrGiven((LivingEntity) entity, ETF_returnOptifineOrVanillaIdentifier(texture.toString()).toString(), id);
                    } else {
                        return ETF_returnBlinkIdOrGiven((LivingEntity) entity, texture.toString(), id);
                    }
                }
            }
        }
        return ETF_returnBlinkIdOrGiven((LivingEntity) entity, texture.toString(), id);
    }

    default void ETF_GeneralEmissiveRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, String texturePath, Model model) {
        ETF_GeneralEmissiveRender(matrixStack, vertexConsumerProvider, new Identifier(texturePath), model);
    }

    //will set and render emissive texture for any texture and model
    default void ETF_GeneralEmissiveRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, Identifier texture, Model model) {
        if (ETFConfigData.enableEmissiveTextures) {
            String fileString = texture.toString();
            if (!ETF_PATH_EmissiveTextureIdentifier.containsKey(fileString)) {
                //creates and sets emissive for texture if it exists
                Identifier fileName_e;
                for (String suffix1 :
                        ETF_emissiveSuffixes) {
                    fileName_e = new Identifier(fileString.replace(".png", suffix1 + ".png"));
                    if (ETF_isExistingFile(fileName_e)) {
                        ETF_PATH_EmissiveTextureIdentifier.put(fileString, fileName_e);
                        break;
                    }
                }
                if (!ETF_PATH_EmissiveTextureIdentifier.containsKey(fileString)) {
                    ETF_PATH_EmissiveTextureIdentifier.put(fileString, null);
                }
            }
            if (ETF_PATH_EmissiveTextureIdentifier.containsKey(fileString)) {
                if (ETF_PATH_EmissiveTextureIdentifier.get(fileString) != null) {
                    VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(ETF_PATH_EmissiveTextureIdentifier.get(fileString), true));
                    //one check most efficient instead of before and after applying
                    if (ETFConfigData.doShadersEmissiveFix) {
                        matrixStack.scale(1.01f, 1.01f, 1.01f);
                        model.render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
                        matrixStack.scale(1f, 1f, 1f);
                    } else {
                        model.render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1.0F);
                    }
                }
            }
        }
    }

}
