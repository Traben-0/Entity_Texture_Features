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
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
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


    default boolean isExistingFile(Identifier id) {
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id);
            try {
                //NativeImage.read(resource.getInputStream());
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

    default boolean checkPathExist(String path) {
        return isExistingFile(new Identifier(path));
    }

    default void resetVisuals() {
        modMessage("Reloading...", false);
        Texture_TotalTrueRandom.clear();
        UUID_randomTextureSuffix.clear();
        Texture_OptifineRandomSettingsPerTexture.clear();
        Texture_OptifineOrTrueRandom.clear();
        optifineOldOrVanilla.clear();// 0,1,2
        ignoreOnePNG.clear();
        UUID_entityAlreadyCalculated.clear();//only time it clears
        UUID_entityAwaitingDataClearing.clear();

        UUID_playerHasFeatures.clear();
        UUID_playerHasEnchant.clear();
        UUID_playerHasEmissive.clear();
        UUID_playerTransparentSkinId.clear();
        UUID_playerSkinDownloadedYet.clear();
        for (HttpURLConnection h :
                URL_HTTPtoDisconnect1.values()) {
            if (h != null) {
                h.disconnect();
            }
        }
        for (HttpURLConnection h :
                URL_HTTPtoDisconnect2.values()) {
            if (h != null) {
                h.disconnect();
            }
        }
        UUID_playerHasCoat.clear();
        URL_HTTPtoDisconnect1.clear();

        PATH_FailedPropertiesToIgnore.clear();

        TEXTURE_HasBlink.clear();
        TEXTURE_HasBlink2.clear();

        UUID_TridentName.clear();

        Texture_Emissive.clear();
        setEmissiveSuffix();

        TEXTURE_VillagerIsExistingFeature.clear();

        mooshroomRedCustomShroom = 0;
        mooshroomBrownCustomShroom = 0;

        registerNativeImageToIdentifier(emptyNativeImage(1,1),"etf:blank.png");
    }

    default void resetSingleData(UUID id) {
        UUID_randomTextureSuffix.remove(id);
    }


    default Properties readProperties(String path) {
        Properties props = new Properties();
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(path));
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

    default NativeImage getNativeImageFromID(Identifier identifier) {
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


    default void processNewRandomTextureCandidate(String vanillaTexturePath) {
        boolean hasProperties = false;
        String properties = "";
        if (checkPathExist(vanillaTexturePath.replace(".png", ".properties").replace("textures", "optifine/random"))) {
            properties = vanillaTexturePath.replace(".png", ".properties").replace("textures", "optifine/random");
            hasProperties = true;
            optifineOldOrVanilla.put(vanillaTexturePath, 0);
        } else if (isExistingFile(new Identifier(vanillaTexturePath.replace(".png", "2.png").replace("textures", "optifine/random")))) {
            optifineOldOrVanilla.put(vanillaTexturePath, 0);
        } else if (checkPathExist(vanillaTexturePath.replace(".png", ".properties").replace("textures/entity", "optifine/mob"))) {
            properties = vanillaTexturePath.replace(".png", ".properties").replace("textures/entity", "optifine/mob");
            hasProperties = true;
            optifineOldOrVanilla.put(vanillaTexturePath, 1);
        } else if (isExistingFile(new Identifier(vanillaTexturePath.replace(".png", "2.png").replace("textures/entity", "optifine/mob")))) {
            optifineOldOrVanilla.put(vanillaTexturePath, 1);
        } else if (checkPathExist(vanillaTexturePath.replace(".png", ".properties"))) {
            properties = vanillaTexturePath.replace(".png", ".properties");
            hasProperties = true;
            optifineOldOrVanilla.put(vanillaTexturePath, 2);
        } else if (isExistingFile(new Identifier(vanillaTexturePath.replace(".png", "2.png")))) {
            optifineOldOrVanilla.put(vanillaTexturePath, 2);
        }

        //no settings just true random
        if (hasProperties && !PATH_FailedPropertiesToIgnore.contains(properties)) {//optifine settings found
            processOptifineTextureCandidate(vanillaTexturePath, properties);
        } else {
            processTrueRandomCandidate(vanillaTexturePath);
        }
    }

    private void processOptifineTextureCandidate(String vanillaTexturePath, String propertiesPath) {
        try {
            ignoreOnePNG.put(vanillaTexturePath, !(isExistingFile(new Identifier(propertiesPath.replace(".properties", "1.png")))));

            Properties props = readProperties(propertiesPath);
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

                    if (props.containsKey("skins." + num) || props.containsKey("textures." + num)) {
                        String dataFromProps = props.containsKey("skins." + num) ? props.getProperty("skins." + num).trim() : props.getProperty("textures." + num).trim();
                        String[] skinData = dataFromProps.split("\s+");
                        ArrayList<Integer> suffixNumbers = new ArrayList<>();
                        for (String data :
                                skinData) {
                            //check if range
                            data = data.trim();
                            if(!data.replaceAll("[^0-9]", "").isEmpty()) {
                                if (data.contains("-")) {
                                    suffixNumbers.addAll(Arrays.asList(getIntRange(data)));
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
                            if(!s.replaceAll("[^0-9]", "").isEmpty()) {
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
                            if(!data.replaceAll("[^0-9]", "").isEmpty()) {
                                if (data.contains("-")) {
                                    heightNumbers.addAll(Arrays.asList(getIntRange(data)));
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
                            List<String> list = new ArrayList<String>();
                            Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(dataFromProps);
                            while (m.find()) {
                                list.add(m.group(1).replace("\"", ""));
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
                            if(!data.replaceAll("[^0-9]", "").isEmpty()) {
                                if (data.contains("-")) {
                                    moonNumbers.addAll(Arrays.asList(getIntRange(data)));
                                } else{
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
                    }else if (props.containsKey("block." + num)) {
                        blocks = props.getProperty("block." + num).trim().split("\s+");
                    }


                    if (suffixes.length != 0) {
                        allCasesForTexture.add(new randomCase(suffixes, weights, biomes, heights, names, professions, collarColours, baby, weather, health, moon, daytime,blocks));
                    }
                }
                if (!allCasesForTexture.isEmpty()) {
                    Texture_OptifineRandomSettingsPerTexture.put(vanillaTexturePath, allCasesForTexture);
                    Texture_OptifineOrTrueRandom.put(vanillaTexturePath, true);
                } else {
                    modMessage("Ignoring properties file that failed to load any cases @ " + propertiesPath, false);
                    PATH_FailedPropertiesToIgnore.add(propertiesPath);
                }
            } else {//properties file is null
                modMessage("Ignoring properties file that was null @ " + propertiesPath, false);
                PATH_FailedPropertiesToIgnore.add(propertiesPath);
            }
        } catch (Exception e) {
            modMessage("Ignoring properties file that caused Exception @ " + propertiesPath+e, false);
            PATH_FailedPropertiesToIgnore.add(propertiesPath);
        }
    }

    default Integer[] getIntRange(String rawRange) {
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
                modMessage("Optifine properties failed to load: Texture heights range has a problem in properties file. this has occurred for value \"" + rawRange.replace("N", "-") + "\"", false);
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

    default void testCases(String vanillaPath, UUID id, Entity entity, boolean isUpdate) {
        for (randomCase test :
                Texture_OptifineRandomSettingsPerTexture.get(vanillaPath)) {

            //skip if its only an update and case is not updatables
            if (!(isUpdate && test.caseHasNonUpdatables)) {
                if (test.testEntity((LivingEntity) entity, UUID_entityAlreadyCalculated.contains(id))) {
                    UUID_randomTextureSuffix.put(id, test.getWeightedSuffix(id, ignoreOnePNG.get(vanillaPath)));
                    Identifier tested = returnOptifineOrVanillaIdentifier(vanillaPath, UUID_randomTextureSuffix.get(id));

                    if (!isExistingFile(tested)) {
                        UUID_randomTextureSuffix.put(id, 0);
                    }
                    break;
                }
            }
        }
        if (!hasUpdatableRandomCases.containsKey(id))
            hasUpdatableRandomCases.put(id, false);
    }

    default void modMessage(String message, boolean inChat) {
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

    default String returnOptifineOrVanillaPath(String vanillaPath, int randomId, String emissiveSuffx) {


        String append = (randomId == 0 ? "":randomId) + emissiveSuffx + ".png";
        return switch (optifineOldOrVanilla.get(vanillaPath)) {
            case 0 -> vanillaPath.replace(".png",append ).replace("textures", "optifine/random");
            case 1 -> vanillaPath.replace(".png", append).replace("textures/entity", "optifine/mob");
            default -> vanillaPath.replace(".png", append);
        };
    }

    default Identifier returnOptifineOrVanillaIdentifier(String vanillaPath, int randomId) {
        return new Identifier(returnOptifineOrVanillaPath(vanillaPath, randomId, ""));
    }
    default Identifier returnOptifineOrVanillaIdentifier(String vanillaPath) {
        return new Identifier(returnOptifineOrVanillaPath(vanillaPath, 0, ""));
    }

    private void processTrueRandomCandidate(String vanillaPath) {
        boolean keepGoing = false;
        //ArrayList<String> allTextures = new ArrayList<String>();
        String checkPath;
        String checkPathOptifineFormat;
        String checkPathOldRandomFormat;
        //first iteration longer
        int successCount = 0;
        //allTextures.add(vanillaPath);
        //can start from either texture1.png or texture2.png check both first
        //check if texture1.png is used
        checkPath = vanillaPath.replace(".png", "1.png");
        checkPathOldRandomFormat = vanillaPath.replace(".png", "1.png").replace("textures/entity", "optifine/mob");
        checkPathOptifineFormat = vanillaPath.replace(".png", "1.png").replace("textures", "optifine/random");
        if (isExistingFile(new Identifier(checkPathOptifineFormat))) {
            optifineOldOrVanilla.put(vanillaPath, 0);
            ignoreOnePNG.put(vanillaPath, false);
            //successCount++;
        } else if (isExistingFile(new Identifier(checkPathOldRandomFormat))) {
            optifineOldOrVanilla.put(vanillaPath, 1);
            ignoreOnePNG.put(vanillaPath, false);
            //successCount++;
        } else if (isExistingFile(new Identifier(checkPath))) {
            optifineOldOrVanilla.put(vanillaPath, 2);
            ignoreOnePNG.put(vanillaPath, false);
            //successCount++;
        } else {
            ignoreOnePNG.put(vanillaPath, true);
        }

        //check if texture 2.png is used
        checkPath = vanillaPath.replace(".png", "2.png");
        checkPathOldRandomFormat = vanillaPath.replace(".png", "2.png").replace("textures/entity", "optifine/mob");
        checkPathOptifineFormat = vanillaPath.replace(".png", "2.png").replace("textures", "optifine/random");
        if (isExistingFile(new Identifier(checkPathOptifineFormat))) {
            optifineOldOrVanilla.put(vanillaPath, 0);
            keepGoing = true;
            successCount = 2;
        } else if (isExistingFile(new Identifier(checkPathOldRandomFormat))) {
            optifineOldOrVanilla.put(vanillaPath, 1);
            keepGoing = true;
            successCount = 2;
        } else if (isExistingFile(new Identifier(checkPath))) {
            optifineOldOrVanilla.put(vanillaPath, 2);
            keepGoing = true;
            successCount = 2;
        }
        //texture3.png and further optimized iterations
        int count = 2;
        while (keepGoing) {
            count++;
            if (optifineOldOrVanilla.get(vanillaPath) == 0) {
                checkPath = vanillaPath.replace(".png", (count + ".png")).replace("textures", "optifine/random");
            } else if (optifineOldOrVanilla.get(vanillaPath) == 1) {
                checkPath = vanillaPath.replace(".png", (count + ".png")).replace("textures/entity", "optifine/mob");
            } else {
                checkPath = vanillaPath.replace(".png", (count + ".png"));
            }
            keepGoing = isExistingFile(new Identifier(checkPath));
            if (keepGoing) successCount++;
        }
        //true if any random textures at all

        Texture_TotalTrueRandom.put(vanillaPath, successCount);
        Texture_OptifineOrTrueRandom.put(vanillaPath, false);

    }


    default void setEmissiveSuffix() {
        try {
            Properties suffix = readProperties("optifine/emissive.properties");
            if (suffix.isEmpty()) {
                suffix = readProperties("textures/emissive.properties");
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
            emissiveSuffix = builder.toArray(new String[0]);
            if (emissiveSuffix.length == 0) {
                modMessage("Error! Default emissive suffix '_e' used", false);
                emissiveSuffix = new String[]{"_e"};
            }
        } catch (Exception e) {
            modMessage("Error! default emissive suffix '_e' used", false);
            emissiveSuffix = new String[]{"_e"};
        }
    }

    default void saveConfig() {
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
            modMessage("Config could not be saved", false);
        }
    }

    default void checkPlayerForSkinFeatures(UUID id, PlayerEntity player) {
        //if on an enemy team option to disable skin features loading
        if (ETFConfigData.skinFeaturesEnabled
                && (ETFConfigData.enableEnemyTeamPlayersSkinFeatures
                || (player.isTeammate(MinecraftClient.getInstance().player)
                || player.getScoreboardTeam() == null))
        ) {
            UUID_playerSkinDownloadedYet.put(id, false);
            UUID_playerHasCape.put(id, ((AbstractClientPlayerEntity) player).canRenderCapeTexture());
            getSkin(player);
        }
    }

    private void getSkin(PlayerEntity player) {
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
                    }catch(Exception e){
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

            downloadImageFromUrl(player, url, "VANILLA_SKIN", capeurl);
        } catch (Exception e) {
            //
            modMessage("Player skin {" + id + "} unavailable for feature check. "+e, false);
            UUID_playerHasFeatures.put(id, false);
        }
    }

    private void downloadImageFromUrl(PlayerEntity player, String url, String sendFileToMethodKey) {
        downloadImageFromUrl(player, url, sendFileToMethodKey, null, false);
    }

    private void downloadImageFromUrl(PlayerEntity player, String url, String sendFileToMethodKey, String url2) {
        downloadImageFromUrl(player, url, sendFileToMethodKey, url2, false);
    }

    private void downloadImageFromUrl(PlayerEntity player, String url, String sendFileToMethodKey, @Nullable String url2, boolean isFile) {
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
                                directFileFromUrlToMethod(player, read, sendFileToMethodKey);
                            } else {
                                NativeImage one = this.loadTexture(inputStream);
                                NativeImage two = null;
                                if (newHas2) {
                                    two = this.loadTexture(finalInputStreamCape);
                                }
                                if (one != null) {
                                    directImageFromUrlToMethod(player, one, sendFileToMethodKey, two);
                                } else {
                                    modMessage("downloading image failed", false);
                                }
                            }
                            if (URL_HTTPtoDisconnect1.containsKey(url)) {
                                if (URL_HTTPtoDisconnect1.get(url) != null) {
                                    URL_HTTPtoDisconnect1.get(url).disconnect();
                                }
                                URL_HTTPtoDisconnect1.remove(url);
                            }
                            if (URL_HTTPtoDisconnect2.containsKey(url2)) {
                                if (URL_HTTPtoDisconnect2.get(url2) != null) {
                                    URL_HTTPtoDisconnect2.get(url2).disconnect();
                                }
                                URL_HTTPtoDisconnect2.remove(url2);
                            }
                        });

                    }
                } catch (Exception var6) {
                    URL_HTTPtoDisconnect1.put(url, httpURLConnection);
                    URL_HTTPtoDisconnect2.put(url2, httpURLConnection2);
                } finally {
                    URL_HTTPtoDisconnect1.put(url, httpURLConnection);
                    URL_HTTPtoDisconnect2.put(url2, httpURLConnection2);
                }

            }, Util.getMainWorkerExecutor());

        } catch (Exception e) {
            //
        }
    }

    private void directFileFromUrlToMethod(PlayerEntity player, String fileString, String sendFileToMethodKey) {
        //switch
        if (fileString != null) {
            if (sendFileToMethodKey.equals("ETF_CAPE")) {
                System.out.println(fileString);
            }
        }

    }

    private void directImageFromUrlToMethod(PlayerEntity player, NativeImage image, String sendFileToMethodKey, @Nullable NativeImage image2) {
        //switch
        UUID id = player.getUuid();
        if (sendFileToMethodKey.equals("VANILLA_SKIN")) {
            if (image != null) {
                skinLoaded(player, image, image2);
            } else {
                modMessage("Player skin {" + player.getName().getString() + "} unavailable for feature check", false);
                UUID_playerHasFeatures.put(id, false);
            }
        }else if (sendFileToMethodKey.equals("THIRD_PARTY_CAPE")) {
            if (image != null) {
                //optifine resizes them for space cause expensive servers I guess
                if (image.getWidth() % image.getHeight() != 0){
                    registerNativeImageToIdentifier(resizeOptifineImage(image), SKIN_NAMESPACE + id + "_cape.png");
                }else {
                    registerNativeImageToIdentifier(image, SKIN_NAMESPACE + id + "_cape.png");
                }
                UUID_playerHasCustomCape.put(id, true);
            } else {
                modMessage("Player skin {" + player.getName().getString() + "} no THIRD_PARTY_CAPE Found", false);
                //registerNativeImageToIdentifier(getNativeImageFromID(new Identifier("etf:capes/error.png")), SKIN_NAMESPACE + id + "_cape.png");
                UUID_playerHasCustomCape.put(id, false);
            }

        }
    }

    private NativeImage resizeOptifineImage(NativeImage image){
        int newWidth = 64;
        while (newWidth < image.getWidth()){
            newWidth = newWidth + newWidth;
        }
        int newHeight = newWidth / 2;
        NativeImage resizedImage = emptyNativeImage(newWidth,newHeight);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                resizedImage.setColor(x,y,image.getColor(x,y));
            }
        }
        return resizedImage;
    }

    private NativeImage loadTexture(InputStream stream) {
        NativeImage nativeImage = null;

        try {
            nativeImage = NativeImage.read(stream);

        } catch (Exception var4) {
            modMessage("failed 165165651" + var4, false);
        }

        return nativeImage;
    }


    private int getSkinPixelColourToNumber(int color) {
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

    private void skinLoaded(PlayerEntity player, NativeImage skin, @Nullable NativeImage cape) {
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
                modMessage("Found Player {" + id + "} with texture features in skin.", false);
                UUID_playerHasFeatures.put(id, true);
                //find what features
                //pink = -65281, blue = -256
                //            pink   cyan     red       green      brown    blue     orange     yellow
                //colours = -65281, -256, -16776961, -16711936, -16760705, -65536, -16744449, -14483457

                //check Choice Box
                int[] choiceBoxChoices = {getSkinPixelColourToNumber(skin.getColor(52, 16)),
                        getSkinPixelColourToNumber(skin.getColor(52, 17)),
                        getSkinPixelColourToNumber(skin.getColor(52, 18)),
                        getSkinPixelColourToNumber(skin.getColor(52, 19)),
                        getSkinPixelColourToNumber(skin.getColor(53, 16)),
                        getSkinPixelColourToNumber(skin.getColor(53, 17))};

                //villager nose check
                boolean noseUpper = (getSkinPixelColourToNumber(skin.getColor(43, 13)) == 666 && getSkinPixelColourToNumber(skin.getColor(44, 13)) == 666 &&
                        getSkinPixelColourToNumber(skin.getColor(43, 14)) == 666 && getSkinPixelColourToNumber(skin.getColor(44, 14)) == 666 &&
                        getSkinPixelColourToNumber(skin.getColor(43, 15)) == 666 && getSkinPixelColourToNumber(skin.getColor(44, 15)) == 666);
                boolean noseLower = (getSkinPixelColourToNumber(skin.getColor(11, 13)) == 666 && getSkinPixelColourToNumber(skin.getColor(12, 13)) == 666 &&
                        getSkinPixelColourToNumber(skin.getColor(11, 14)) == 666 && getSkinPixelColourToNumber(skin.getColor(12, 14)) == 666 &&
                        getSkinPixelColourToNumber(skin.getColor(11, 15)) == 666 && getSkinPixelColourToNumber(skin.getColor(12, 15)) == 666);
                if (noseUpper) {
                    deletePixels(skin, 43, 13, 44, 15);
                }
                UUID_playerHasVillagerNose.put(id, noseLower || noseUpper);

                //check for coat bottom
                //pink to copy coat    light blue to remove from legs
                NativeImage coatSkin = null;
                int controllerCoat = choiceBoxChoices[1];
                if (controllerCoat >= 1 && controllerCoat <= 8) {
                    int lengthOfCoat = choiceBoxChoices[2] - 1;
                    Identifier coatID = new Identifier(SKIN_NAMESPACE + id + "_coat.png");
                    coatSkin = getOrRemoveCoatTexture(skin, lengthOfCoat, controllerCoat >= 5);
                    registerNativeImageToIdentifier(coatSkin, coatID.toString());
                    UUID_playerHasCoat.put(id, true);
                    if (controllerCoat == 2 || controllerCoat == 4 || controllerCoat == 6 || controllerCoat == 8) {
                        //delete original pixel from skin
                        deletePixels(skin, 4, 32, 7, 35);
                        deletePixels(skin, 4, 48, 7, 51);
                        deletePixels(skin, 0, 36, 15, 36 + lengthOfCoat);
                        deletePixels(skin, 0, 52, 15, 52 + lengthOfCoat);
                    }
                    //red or green make fat coat
                    UUID_playerHasFatCoat.put(id, controllerCoat == 3 || controllerCoat == 4 || controllerCoat == 7 || controllerCoat == 8);


                } else {
                    UUID_playerHasCoat.put(id, false);
                }
                //check for transparency options
                //System.out.println("about to check");
                if (ETFConfigData.skinFeaturesEnableTransparency) {
                    if (canTransparentSkin(skin)) {
                        Identifier transId = new Identifier(SKIN_NAMESPACE + id + "_transparent.png");
                        UUID_playerTransparentSkinId.put(id, transId);
                        registerNativeImageToIdentifier(skin, transId.toString());

                    } else {
                        modMessage("Skin was too transparent or had other problems", false);
                    }
                }

                //blink
                NativeImage blinkSkinFile = null;
                NativeImage blinkSkinFile2 = null;
                int blinkChoice = choiceBoxChoices[0];
                if (blinkChoice >= 1 && blinkChoice <= 5) {
                    //check if lazy blink
                    TEXTURE_HasBlink.put(SKIN_NAMESPACE + id + ".png", true);
                    if (blinkChoice <= 2) {
                        //blink 1 frame if either pink or blue optional
                        blinkSkinFile = returnOptimizedBlinkFace(skin, getSkinPixelBounds("face1"), 1, getSkinPixelBounds("face3"));

                        registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");

                        //blink is 2 frames with blue optional
                        if (blinkChoice == 2) {
                            blinkSkinFile2 = returnOptimizedBlinkFace(skin, getSkinPixelBounds("face2"), 1, getSkinPixelBounds("face4"));
                            TEXTURE_HasBlink2.put(SKIN_NAMESPACE + id + ".png", true);
                            registerNativeImageToIdentifier(blinkSkinFile2, SKIN_NAMESPACE + id + "_blink2.png");
                        } else {
                            TEXTURE_HasBlink2.put(SKIN_NAMESPACE + id + ".png", false);
                        }
                    } else {//optimized blink
                        int eyeHeightTopDown = choiceBoxChoices[3];
                        if(eyeHeightTopDown > 8 || eyeHeightTopDown < 1){
                            eyeHeightTopDown = 1;
                        }
                        //optimized 1p high eyes
                        if (blinkChoice == 3) {
                            blinkSkinFile = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEyeSmall"), eyeHeightTopDown);

                            registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");

                        } else if (blinkChoice == 4) {
                            blinkSkinFile = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye2High"), eyeHeightTopDown);
                            blinkSkinFile2 = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye2High_second"), eyeHeightTopDown);
                            TEXTURE_HasBlink2.put(SKIN_NAMESPACE + id + ".png", true);

                            registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");
                            registerNativeImageToIdentifier(blinkSkinFile2, SKIN_NAMESPACE + id + "_blink2.png");
                        } else /*if( blinkChoice == 5)*/ {
                            blinkSkinFile = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye4High"), eyeHeightTopDown);
                            blinkSkinFile2 = returnOptimizedBlinkFace(skin, getSkinPixelBounds("optimizedEye4High_second"), eyeHeightTopDown);
                            TEXTURE_HasBlink2.put(SKIN_NAMESPACE + id + ".png", true);
                            registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");
                            registerNativeImageToIdentifier(blinkSkinFile2, SKIN_NAMESPACE + id + "_blink2.png");
                        }
                    }


                }
                if(!TEXTURE_HasBlink.containsKey(SKIN_NAMESPACE + id + ".png")) {
                    TEXTURE_HasBlink.put(SKIN_NAMESPACE + id + ".png", false);
                }
                if(!TEXTURE_HasBlink2.containsKey(SKIN_NAMESPACE + id + ".png")){
                    TEXTURE_HasBlink2.put(SKIN_NAMESPACE + id + ".png", false);
                }

                //check for cape recolor
                int capeChoice1 = choiceBoxChoices[4];
                int capeChoice2 = choiceBoxChoices[5];
                // custom cape data experiment
                // https://drive.google.com/uc?export=download&id=1rn1swLadqdMiLirz9Nrae0_VHFrTaJQe
                //downloadImageFromUrl(player, "https://drive.google.com/uc?export=download&id=1rn1swLadqdMiLirz9Nrae0_VHFrTaJQe", "ETF_CAPE",null,true);
                if ((capeChoice1 >= 1 && capeChoice1 <= 3) || capeChoice1 == 666) {
                    switch (capeChoice1) {
                        case 1 -> {//custom in skin
                            cape = returnCustomTexturedCape(skin);
                        }
                        case 2 -> {
                            cape = null;
                            // minecraft capes mod
                            //https://minecraftcapes.net/profile/fd22e573178c415a94fee476b328abfd/cape/
                            downloadImageFromUrl(player, "https://minecraftcapes.net/profile/" + player.getUuidAsString().replace("-", "") + "/cape/", "THIRD_PARTY_CAPE");
                        }
                        case 3 -> {
                            cape = null;
                            //  https://optifine.net/capes/Benjamin.png
                            downloadImageFromUrl(player, "https://optifine.net/capes/" + player.getName().getString() + ".png", "THIRD_PARTY_CAPE");
                        }
                        case 666 -> {
                            cape = getNativeImageFromID(new Identifier("etf:capes/error.png"));
                        }
                        default -> {
                            //cape = getNativeImageFromID(new Identifier("etf:capes/blank.png"));
                        }
                    }
                }
                if (cape != null){
                        if ((capeChoice1 >= 1 && capeChoice1 <= 3) || capeChoice1 == 666){//custom chosen
                        registerNativeImageToIdentifier(cape, SKIN_NAMESPACE + id + "_cape.png");
                        UUID_playerHasCustomCape.put(id, true);
                    }
                }
                if (!UUID_playerHasCustomCape.containsKey(id)) {
                    UUID_playerHasCustomCape.put(id, false);
                }


                //check for marker choices
                //  1 = Emissives,  2 = Enchanted
                List<Integer> markerChoices = List.of(getSkinPixelColourToNumber(skin.getColor(1, 17)),
                        getSkinPixelColourToNumber(skin.getColor(1, 18)),
                        getSkinPixelColourToNumber(skin.getColor(2, 17)),
                        getSkinPixelColourToNumber(skin.getColor(2, 18)));

                //enchanted
                UUID_playerHasEnchant.put(id, markerChoices.contains(2));
                if (markerChoices.contains(2)) {
                    //System.out.println("choice " + (markerChoices.indexOf(2) + 1));
                    int[] boxChosenBounds = getSkinPixelBounds("marker" + (markerChoices.indexOf(2) + 1));
                    NativeImage check = returnMatchPixels(skin, boxChosenBounds);
                    if (check != null) {
                        registerNativeImageToIdentifier(check, SKIN_NAMESPACE + id + "_enchant.png");
                        if (blinkSkinFile != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, this::emptyNativeImage), SKIN_NAMESPACE + id + "_blink_enchant.png");
                        }
                        if (blinkSkinFile2 != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile2, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, this::emptyNativeImage), SKIN_NAMESPACE + id + "_blink2_enchant.png");
                        }
                        if (coatSkin != null) {
                            NativeImage checkCoat = returnMatchPixels(coatSkin, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCoat, this::emptyNativeImage), SKIN_NAMESPACE + id + "_coat_enchant.png");
                        }

                       // NativeImage checkCape = returnMatchPixels(skin, boxChosenBounds,cape);
                       // registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCape, this::emptyNativeImage), SKIN_NAMESPACE + id + "_cape_enchant.png");

                    } else {
                        UUID_playerHasEnchant.put(id, false);
                    }

                }
                //emissives
                UUID_playerHasEmissive.put(id, markerChoices.contains(1));
                if (markerChoices.contains(1)) {
                    int[] boxChosenBounds = getSkinPixelBounds("marker" + (markerChoices.indexOf(1) + 1));
                    NativeImage check = returnMatchPixels(skin, boxChosenBounds);
                    if (check != null) {
                        registerNativeImageToIdentifier(check, SKIN_NAMESPACE + id + "_e.png");
                        if (blinkSkinFile != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, this::emptyNativeImage), SKIN_NAMESPACE + id + "_blink_e.png");
                        }
                        if (blinkSkinFile2 != null) {
                            NativeImage checkBlink = returnMatchPixels(blinkSkinFile2, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkBlink, this::emptyNativeImage), SKIN_NAMESPACE + id + "_blink2_e.png");
                        }
                        if (coatSkin != null) {
                            NativeImage checkCoat = returnMatchPixels(coatSkin, boxChosenBounds);
                            registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCoat, this::emptyNativeImage), SKIN_NAMESPACE + id + "_coat_e.png");
                        }

                      //  NativeImage checkCape = returnMatchPixels(skin, boxChosenBounds,cape);
                       // registerNativeImageToIdentifier(Objects.requireNonNullElseGet(checkCape, this::emptyNativeImage), SKIN_NAMESPACE + id + "_cape_e.png");

                    } else {
                        UUID_playerHasEmissive.put(id, false);
                    }

                }

            } else {
                UUID_playerHasFeatures.put(id, false);
            }
        } else { //http failed
            UUID_playerHasFeatures.put(id, false);
        }
        UUID_playerSkinDownloadedYet.put(id, true);
    }

    private NativeImage emptyNativeImage() {
        return emptyNativeImage(64, 64);
    }

    private NativeImage emptyNativeImage(int Width, int Height) {
        NativeImage empty = new NativeImage(Width, Height, false);
        empty.fillRect(0, 0, Width, Height, 0);
        return empty;
    }

    private int[] getSkinPixelBounds(String choiceKey) {
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

    private NativeImage returnCustomTexturedCape(NativeImage skin){
        NativeImage cape = emptyNativeImage(64,32);
        cape.copyFrom(getNativeImageFromID(new Identifier("etf:capes/default_elytra.png")));
        copyToPixels(skin,cape,getSkinPixelBounds("cape1"),1,1);
        copyToPixels(skin,cape,getSkinPixelBounds("cape1"),12,1);
        copyToPixels(skin,cape,getSkinPixelBounds("cape2"),1,5);
        copyToPixels(skin,cape,getSkinPixelBounds("cape2"),12,5);
        copyToPixels(skin,cape,getSkinPixelBounds("cape3"),1,9);
        copyToPixels(skin,cape,getSkinPixelBounds("cape3"),12,9);
        copyToPixels(skin,cape,getSkinPixelBounds("cape4"),1,13);
        copyToPixels(skin,cape,getSkinPixelBounds("cape4"),12,13);
        copyToPixels(skin,cape,getSkinPixelBounds("cape5.1"),9,1);
        copyToPixels(skin,cape,getSkinPixelBounds("cape5.1"),20,1);
        copyToPixels(skin,cape,getSkinPixelBounds("cape5.2"),9,5);
        copyToPixels(skin,cape,getSkinPixelBounds("cape5.2"),20,5);
        copyToPixels(skin,cape,getSkinPixelBounds("cape5.3"),9,9);
        copyToPixels(skin,cape,getSkinPixelBounds("cape5.3"),20,9);
        copyToPixels(skin,cape,getSkinPixelBounds("cape5.4"),9,13);
        copyToPixels(skin,cape,getSkinPixelBounds("cape5.4"),20,13);

        copyToPixels(cape,cape,getSkinPixelBounds("capeVertL"),0,1);
        copyToPixels(cape,cape,getSkinPixelBounds("capeVertR"),11,1);
        copyToPixels(cape,cape,getSkinPixelBounds("capeHorizL"),1,0);
        copyToPixels(cape,cape,getSkinPixelBounds("capeHorizR"),11,0);

        return cape;
    }

    private NativeImage recolorCape(NativeImage skin, NativeImage cape, int[] originalColorBounds, int[] newColorBounds) {
        NativeImage newCape = emptyNativeImage(cape.getWidth(), cape.getHeight());
        newCape.copyFrom(cape);
        Map<Integer, Integer> originalsToNew = new HashMap<>();
        for (int x = originalColorBounds[0]; x <= originalColorBounds[2]; x++) {
            for (int y = originalColorBounds[1]; y <= originalColorBounds[3]; y++) {
                if (skin.getColor(x, y) != 0) {
                    originalsToNew.put(skin.getColor(x, y), skin.getColor(x - originalColorBounds[0] + newColorBounds[0], y - originalColorBounds[1] + newColorBounds[1]));
                }
            }
        }
        if (originalsToNew.isEmpty()) {
            return null;
        }
        for (int x = 0; x <= cape.getWidth() - 1; x++) {
            for (int y = 0; y <= cape.getHeight() - 1; y++) {
                int color = cape.getColor(x, y);
                if (color != 0 && originalsToNew.containsKey(color)) {
                    newCape.setColor(x, y, originalsToNew.get(color));
                }
            }
        }
        return newCape;
    }

    private NativeImage getOrRemoveCoatTexture(NativeImage skin, int lengthOfCoat, boolean ignoreTopTexture) {

        NativeImage coat = new NativeImage(64, 64, false);
        coat.fillRect(0, 0, 64, 64, 0);

        //top
        if (!ignoreTopTexture) {
            copyToPixels(skin, coat, 4, 32, 7, 35 + lengthOfCoat, 20, 32);
            copyToPixels(skin, coat, 4, 48, 7, 51 + lengthOfCoat, 24, 32);
        }
        //sides
        copyToPixels(skin, coat, 0, 36, 7, 36 + lengthOfCoat, 16, 36);
        copyToPixels(skin, coat, 12, 36, 15, 36 + lengthOfCoat, 36, 36);
        copyToPixels(skin, coat, 4, 52, 15, 52 + lengthOfCoat, 24, 36);
        //ENCHANT AND EMISSIVES
        copyToPixels(skin, coat, 56, 16, 63, 47, 0, 0);
        return coat;

    }

    // modifiers are distance from x1,y1 to copy
    private void copyToPixels(NativeImage source, NativeImage dest, int[] bounds, int copyToX, int CopyToY) {
        copyToPixels(source, dest, bounds[0], bounds[1], bounds[2], bounds[3], copyToX, CopyToY);
    }

    private void copyToPixels(NativeImage source, NativeImage dest, int x1, int y1, int x2, int y2, int copyToX, int copyToY) {
        int copyToXRelative = copyToX - x1;
        int copyToYRelative = copyToY - y1;
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                dest.setColor(x + copyToXRelative, y + copyToYRelative, source.getColor(x, y));
            }
        }
    }

    private void deletePixels(NativeImage source, int x1, int y1, int x2, int y2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                source.setColor(x, y, 0);
            }
        }
    }

    private void registerNativeImageToIdentifier(NativeImage img, String identifierPath) {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        NativeImageBackedTexture bob = new NativeImageBackedTexture(img);
        textureManager.registerTexture(new Identifier(identifierPath), bob);

    }

    private int countTransparentInBox(NativeImage img, int x1, int y1, int x2, int y2) {
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

    private boolean canTransparentSkin(NativeImage skin) {
        if (ETFConfigData.skinFeaturesEnableFullTransparency) {
            return true;
        } else {
            int countTransparent = 0;
            //map of bottom skin layer in cubes
            countTransparent += countTransparentInBox(skin, 8, 0, 23, 15);
            countTransparent += countTransparentInBox(skin, 0, 20, 55, 31);
            countTransparent += countTransparentInBox(skin, 0, 8, 7, 15);
            countTransparent += countTransparentInBox(skin, 24, 8, 31, 15);
            countTransparent += countTransparentInBox(skin, 0, 16, 11, 19);
            countTransparent += countTransparentInBox(skin, 20, 16, 35, 19);
            countTransparent += countTransparentInBox(skin, 44, 16, 51, 19);
            countTransparent += countTransparentInBox(skin, 20, 48, 27, 51);
            countTransparent += countTransparentInBox(skin, 36, 48, 43, 51);
            countTransparent += countTransparentInBox(skin, 16, 52, 47, 63);
            //do not allow skins under 40% ish total opacity
            //1648 is total pixels that are not allowed transparent by vanilla
            int average = (countTransparent / 1648); // should be 0 to 256
            //System.out.println("average ="+average);
            return average >= 100;
        }
    }

    private NativeImage returnOptimizedBlinkFace(NativeImage baseSkin, int[] eyeBounds, int eyeHeightFromTopDown) {
        return returnOptimizedBlinkFace(baseSkin, eyeBounds, eyeHeightFromTopDown, null);
    }

    private NativeImage returnOptimizedBlinkFace(NativeImage baseSkin, int[] eyeBounds, int eyeHeightFromTopDown, int[] secondLayerBounds) {
        NativeImage texture = new NativeImage(64, 64, false);
        texture.copyFrom(baseSkin);
        //copy face
        copyToPixels(baseSkin, texture, eyeBounds, 8, 8 + (eyeHeightFromTopDown - 1));
        //copy face overlay
        if (secondLayerBounds != null) {
            copyToPixels(baseSkin, texture, secondLayerBounds, 40, 8 + (eyeHeightFromTopDown - 1));
        }
        return texture;
    }

    @Nullable
    private NativeImage returnMatchPixels(NativeImage baseSkin, int[] boundsToCheck) {
        return  returnMatchPixels( baseSkin, boundsToCheck, null);
    }

    @Nullable
    private NativeImage returnMatchPixels(NativeImage baseSkin, int[] boundsToCheck,@Nullable NativeImage second) {
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
            if (!secondImage){
                texture.copyFrom(baseSkin);
            }else{
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
}
