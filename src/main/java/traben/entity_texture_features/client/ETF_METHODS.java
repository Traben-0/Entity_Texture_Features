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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

public interface ETF_METHODS<T extends LivingEntity> {


    default boolean isExistingFile(Identifier id) {
        try {
            //Resource resource = resourceManager.getResource(id);
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(id);
            try {
                NativeImage.read(resource.getInputStream());
                resource.close();
                return true;
            } catch (IOException e) {
                //System.out.println("1"+id.getPath());
                resource.close();
                return false;
            }
        } catch (IOException f) {
            //System.out.println("2="+id.getPath()+f);
            return false;
        }
    }

    default void resetVisuals() {
        modMessage("Reloading... (this may change all random mobs)", false);
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
                UUID_HTTPtoDisconnect.values()) {
            if (h != null) {
                h.disconnect();
            }
        }
        UUID_HTTPtoDisconnect.clear();

        UUID_HasBlink.clear();
        UUID_HasBlink2.clear();

        UUID_TridentName.clear();

        Texture_Emissive.clear();
        setEmissiveSuffix();
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
            }
        } catch (Exception e) {
            //
        }
        System.out.println(props);
        return props;
//        try {
//            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(path));
//            try {
//                InputStream in = resource.getInputStream();
//                StringBuilder str = new StringBuilder();
//                int content;
//                while ((content = in.read()) != -1) {
//                    str.append((char) content);
//                }
//                resource.close();
//                return str.toString();
//
//            } catch (Exception e) {
//                resource.close();
//                return "";
//            }
//        } catch (Exception e) {
//            return "";
//        }
    }

    default boolean checkPropertiesExist(String path) {
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(path));
            try {
                resource.getInputStream();
                resource.close();
                return true;
            } catch (Exception e) {
                resource.close();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    default void processNewRandomTextureCandidate(String vanillaTexturePath) {
        boolean hasProperties = false;
        //gonna ignore this for now
        //if (vanillaTexturePath.contains("wolf")){
        //    vanillaTexturePath = vanillaTexturePath.replace("wolf_angry","wolf")
        //    vanillaTexturePath = vanillaTexturePath.replace("wolf_tame","wolf")
        //}
        String properties = "";
        if (checkPropertiesExist(vanillaTexturePath.replace(".png", ".properties").replace("textures", "optifine/random"))) {
            properties = vanillaTexturePath.replace(".png", ".properties").replace("textures", "optifine/random");
            hasProperties = true;
            optifineOldOrVanilla.put(vanillaTexturePath, 0);
        } else if (isExistingFile(new Identifier(vanillaTexturePath.replace(".png", "2.png").replace("textures", "optifine/random")))) {
            optifineOldOrVanilla.put(vanillaTexturePath, 0);
        } else if (checkPropertiesExist(vanillaTexturePath.replace(".png", ".properties").replace("textures/entity", "optifine/mob"))) {
            properties = vanillaTexturePath.replace(".png", ".properties").replace("textures/entity", "optifine/mob");
            hasProperties = true;
            optifineOldOrVanilla.put(vanillaTexturePath, 1);
        } else if (isExistingFile(new Identifier(vanillaTexturePath.replace(".png", "2.png").replace("textures/entity", "optifine/mob")))) {
            optifineOldOrVanilla.put(vanillaTexturePath, 1);
        } else if (checkPropertiesExist(vanillaTexturePath.replace(".png", ".properties"))) {
            properties = vanillaTexturePath.replace(".png", ".properties");
            hasProperties = true;
            optifineOldOrVanilla.put(vanillaTexturePath, 2);
        } else if (isExistingFile(new Identifier(vanillaTexturePath.replace(".png", "2.png")))) {
            optifineOldOrVanilla.put(vanillaTexturePath, 2);
        }

        //no settings just true random
        if (hasProperties) {//optifine settings found
            processOptifineTextureCandidate(vanillaTexturePath, properties);
        } else {
            processTrueRandomCandidate(vanillaTexturePath);
        }
    }

    private void processOptifineTextureCandidate(String vanillaTexturePath, String propertiesPath) {
        try {
            ignoreOnePNG.put(vanillaTexturePath, !(isExistingFile(new Identifier(propertiesPath.replace(".properties", "1.png")))));

            Properties props = readProperties(propertiesPath);
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

                if (props.containsKey("skins." + num) || props.containsKey("textures." + num)) {
                    String dataFromProps = props.containsKey("skins." + num) ? props.getProperty("skins." + num).trim() : props.getProperty("textures." + num).trim();
                    String[] skinData = dataFromProps.split("\s");
                    ArrayList<Integer> suffixNumbers = new ArrayList<>();
                    for (String data :
                            skinData) {
                        //check if range
                        if (data.contains("-")) {
                            suffixNumbers.addAll(Arrays.asList(getIntRange(data)));
                        } else {
                            suffixNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                        }
                    }
                    suffixes = suffixNumbers.toArray(new Integer[0]);
                }
                if (props.containsKey("weights." + num)) {
                    String dataFromProps = props.getProperty("weights." + num).trim();
                    String[] weightData = dataFromProps.split("\s");
                    ArrayList<Integer> builder = new ArrayList<>();
                    for (String s :
                            weightData) {
                        builder.add(Integer.parseInt(s.replaceAll("[^0-9]", "")));
                    }
                    weights = builder.toArray(new Integer[0]);
                }
                if (props.containsKey("biomes." + num)) {
                    String dataFromProps = props.getProperty("biomes." + num).trim();
                    biomes = dataFromProps.toLowerCase().split("\s");
                }
                if (props.containsKey("heights." + num)) {
                    String dataFromProps = props.getProperty("heights." + num).trim();
                    String[] heightData = dataFromProps.split("\s");
                    ArrayList<Integer> heightNumbers = new ArrayList<>();
                    for (String data :
                            heightData) {
                        //check if range
                        if (data.contains("-")) {
                            heightNumbers.addAll(Arrays.asList(getIntRange(data)));
                        } else {
                            heightNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                        }
                    }
                    heights = heightNumbers.toArray(new Integer[0]);
                }

                if (props.containsKey("names." + num) || props.containsKey("name." + num)) {
                    String dataFromProps = props.containsKey("name." + num) ? props.getProperty("name." + num).trim() : props.getProperty("names." + num).trim();
                    if (dataFromProps.contains("regex:") || dataFromProps.contains("pattern:")) {
                        names = new String[]{dataFromProps};
                    } else {
                        names = dataFromProps.split("\s");
                    }
                }
                if (props.containsKey("professions." + num)) {
                    professions = props.getProperty("professions." + num).trim().split("\s");
                }
                if (props.containsKey("collarColors." + num) || props.containsKey("collarColours." + num)) {
                    collarColours = props.containsKey("collarColors." + num) ? props.getProperty("collarColors." + num).trim().split("\s") : props.getProperty("collarColours." + num).trim().split("\s");
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
                    health = props.getProperty("health." + num).trim().split("\s");
                }
                if (props.containsKey("moonPhase." + num)) {
                    String dataFromProps = props.getProperty("moonPhase." + num).trim();
                    String[] moonData = dataFromProps.split("\s");
                    ArrayList<Integer> moonNumbers = new ArrayList<>();
                    for (String data :
                            moonData) {
                        //check if range
                        if (data.contains("-")) {
                            moonNumbers.addAll(Arrays.asList(getIntRange(data)));
                        } else {
                            moonNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                        }
                    }
                    moon = moonNumbers.toArray(new Integer[0]);
                }
                if (props.containsKey("dayTime." + num)) {
                    daytime = props.getProperty("dayTime." + num).trim().split("\s");
                }

                if (suffixes.length != 0) {
                    allCasesForTexture.add(new randomCase(suffixes, weights, biomes, heights, names, professions, collarColours, baby, weather, health, moon, daytime));
                }
            }
            if (!allCasesForTexture.isEmpty()) {
                Texture_OptifineRandomSettingsPerTexture.put(vanillaTexturePath, allCasesForTexture);
                Texture_OptifineOrTrueRandom.put(vanillaTexturePath, true);
            } else {
                modMessage("Entity Texture Features - reading optifine properties file failed: " + propertiesPath, false);
            }
        } catch (Exception e) {
            //System.out.println(e);
            modMessage("Entity Texture Features - optifine properties failed to load: @(" + propertiesPath + ") Error:" + e, false);
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

    default void testCases(String vanillaPath, UUID id, Entity entity) {
        for (randomCase test :
                Texture_OptifineRandomSettingsPerTexture.get(vanillaPath)) {
            if (test.testEntity((LivingEntity) entity, UUID_entityAlreadyCalculated.contains(id))) {
                UUID_randomTextureSuffix.put(id, test.getWeightedSuffix(id, ignoreOnePNG.get(vanillaPath)));
                Identifier tested = returnOptifineOrVanillaIdentifier(vanillaPath, UUID_randomTextureSuffix.get(id));
                if (!isExistingFile(tested)) {
                    UUID_randomTextureSuffix.put(id, 0);
                }
                break;
            }
        }
        if (!hasUpdatableRandomCases.containsKey(id))
            hasUpdatableRandomCases.put(id, false);
    }

    default void modMessage(String message, boolean inChat) {
        //System.out.println("[Entity Texture Features]: "+message);
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
        return switch (optifineOldOrVanilla.get(vanillaPath)) {
            case 0 -> vanillaPath.replace(".png", randomId + emissiveSuffx + ".png").replace("textures", "optifine/random");
            case 1 -> vanillaPath.replace(".png", randomId + emissiveSuffx + ".png").replace("textures/entity", "optifine/mob");
            default -> vanillaPath.replace(".png", randomId + emissiveSuffx + ".png");
        };
    }

    default Identifier returnOptifineOrVanillaIdentifier(String vanillaPath, int randomId) {
        return new Identifier(returnOptifineOrVanillaPath(vanillaPath, randomId, ""));
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

    default void checkPlayerForSkinFeatures(UUID id, T player) {
        //if on an enemy team option to disable skin features loading
        if (ETFConfigData.skinFeaturesEnabled
                && (ETFConfigData.enableEnemyTeamPlayersSkinFeatures
                || (player.isTeammate(MinecraftClient.getInstance().player)
                || player.getScoreboardTeam() == null))
        ) {
            UUID_playerSkinDownloadedYet.put(id, false);
            getSkin(id, player);
        }
    }

    private void getSkin(UUID id, T player) {
        try {
            String url = "";
            PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(id);
            GameProfile gameProfile = playerListEntry.getProfile();
            PropertyMap texturesMap = gameProfile.getProperties();
            Collection<Property> properties = texturesMap.get("textures");
            for (Property p :
                    properties) {
                JsonObject props = JsonParser.parseString(new String(Base64.getDecoder().decode((p.getValue())))).getAsJsonObject();
                url = ((JsonObject) ((JsonObject) props.get("textures")).get("SKIN")).get("url").getAsString();
                //maybe one day if the EULA allows
                //capeurl = ((JsonObject) ((JsonObject) props.get("textures")).get("CAPE")).get("url").getAsString();
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

            String finalUrl = url;
            //CompletableFuture<?> loader =
            CompletableFuture.runAsync(() -> {
                HttpURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = (HttpURLConnection) (new URL(finalUrl)).openConnection(MinecraftClient.getInstance().getNetworkProxy());
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(false);
                    httpURLConnection.connect();
                    if (httpURLConnection.getResponseCode() / 100 == 2) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        MinecraftClient.getInstance().execute(() -> {
                            NativeImage nativeImage = this.loadTexture(inputStream);
                            if (nativeImage != null) {
                                skinLoaded(nativeImage, id);
                            } else {
                                modMessage("Player skin {" + player.getDisplayName().getString() + "} unavailable for feature check", false);
                                UUID_playerHasFeatures.put(id, false);

                            }
                            if (UUID_HTTPtoDisconnect.containsKey(id)) {
                                UUID_HTTPtoDisconnect.get(id).disconnect();
                                UUID_HTTPtoDisconnect.remove(id);
                            }
                        });

                    }
                } catch (Exception var6) {
                    UUID_HTTPtoDisconnect.put(id, httpURLConnection);
                } finally {
                    UUID_HTTPtoDisconnect.put(id, httpURLConnection);
                }

            }, Util.getMainWorkerExecutor());

        } catch (Exception e) {
            //
        }
    }

    private NativeImage loadTexture(InputStream stream) {
        NativeImage nativeImage = null;

        try {
            nativeImage = NativeImage.read(stream);

        } catch (Exception var4) {
            System.out.println("failed 165165651" + var4);
        }

        return nativeImage;
    }

    private void skinLoaded(NativeImage skin, UUID id) {
        UUID_playerSkinDownloadedYet.put(id, true);
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

                //check for transparency options
                if (ETFConfigData.skinFeaturesEnableTransparency) {
                    if (canTransparentSkin(skin)) {
                        Identifier transId = new Identifier(SKIN_NAMESPACE + id + "_transparent.png");
                        UUID_playerTransparentSkinId.put(id, transId);
                        registerNativeImageToIdentifier(skin, transId.getPath());

                    } else {
                        modMessage("Skin was too transparent or had other problems", false);
                    }
                }

                //pink = -65281, blue = -256
                //blink 1 frame if either pink or blue optional
                NativeImage blinkSkinFile = null;
                if (skin.getColor(52, 16) == -65281 || skin.getColor(52, 16) == -256) {
                    blinkSkinFile = returnBlinkFace(skin, false);
                    UUID_HasBlink.put(id, true);
                    registerNativeImageToIdentifier(blinkSkinFile, SKIN_NAMESPACE + id + "_blink.png");
                } else {
                    UUID_HasBlink.put(id, false);
                }
                //blink is 2 frames with blue optional
                NativeImage blinkSkinFile2 = null;
                if (skin.getColor(52, 16) == -256) {
                    blinkSkinFile2 = returnBlinkFace(skin, true);
                    UUID_HasBlink2.put(id, true);
                    registerNativeImageToIdentifier(blinkSkinFile2, SKIN_NAMESPACE + id + "_blink2.png");
                } else {
                    UUID_HasBlink2.put(id, false);
                }

                NativeImage check = getEnchantedTexture(id, skin);
                if (check != null) {
                    registerNativeImageToIdentifier(check, SKIN_NAMESPACE + id + "_enchant.png");
                    if (blinkSkinFile != null) {
                        NativeImage checkBlink = getEnchantedTexture(id, blinkSkinFile);
                        if (checkBlink != null) {
                            registerNativeImageToIdentifier(checkBlink, SKIN_NAMESPACE + id + "_blink_enchant.png");
                        }
                    }
                    if (blinkSkinFile2 != null) {
                        NativeImage checkBlink = getEnchantedTexture(id, blinkSkinFile2);
                        if (checkBlink != null) {
                            registerNativeImageToIdentifier(checkBlink, SKIN_NAMESPACE + id + "_blink2_enchant.png");
                        }
                    }
                }

                check = getEmissiveTexture(id, skin);
                if (check != null) {
                    registerNativeImageToIdentifier(check, SKIN_NAMESPACE + id + "_e.png");
                    if (blinkSkinFile != null) {
                        NativeImage checkBlink = getEmissiveTexture(id, blinkSkinFile);
                        if (checkBlink != null) {
                            registerNativeImageToIdentifier(checkBlink, SKIN_NAMESPACE + id + "_blink_e.png");
                        }
                    }
                    if (blinkSkinFile2 != null) {
                        NativeImage checkBlink = getEmissiveTexture(id, blinkSkinFile2);
                        if (checkBlink != null) {
                            registerNativeImageToIdentifier(checkBlink, SKIN_NAMESPACE + id + "_blink2_e.png");
                        }
                    }
                }

            } else {
                UUID_playerHasFeatures.put(id, false);
            }
        } else { //http failed
            UUID_playerHasFeatures.put(id, false);
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
        return average >= 100;
    }

    private NativeImage returnBlinkFace(NativeImage baseSkin, boolean isSecondFrame) {
        NativeImage texture = new NativeImage(64, 64, false);
        texture.copyFrom(baseSkin);
        if (isSecondFrame) {
            //copy face 2
            for (int x = 24; x <= 31; x++) {
                for (int y = 0; y <= 7; y++) {
                    texture.setColor(x - 16, y + 8, baseSkin.getColor(x, y));
                }
            }
            //copy face overlay 2
            for (int x = 56; x <= 63; x++) {
                for (int y = 0; y <= 7; y++) {
                    texture.setColor(x - 16, y + 8, baseSkin.getColor(x, y));
                }
            }
        } else {
            //copy face
            for (int x = 0; x <= 7; x++) {
                for (int y = 0; y <= 7; y++) {
                    texture.setColor(x + 8, y + 8, baseSkin.getColor(x, y));
                }
            }
            //copy face overlay
            for (int x = 32; x <= 39; x++) {
                for (int y = 0; y <= 7; y++) {
                    texture.setColor(x + 8, y + 8, baseSkin.getColor(x, y));
                }
            }
        }
        return texture;
    }

    private NativeImage getEnchantedTexture(UUID id, NativeImage baseSkin) {
        NativeImage check = returnMatchPixels(baseSkin, 56, 24, 63, 31);
        UUID_playerHasEnchant.put(id, check != null);
        return check;
    }

    private NativeImage getEmissiveTexture(UUID id, NativeImage baseSkin) {
        NativeImage check = returnMatchPixels(baseSkin, 56, 16, 63, 23);
        UUID_playerHasEmissive.put(id, check != null);
        return check;
    }

    @Nullable
    private NativeImage returnMatchPixels(NativeImage baseSkin, int x1, int y1, int x2, int y2) {
        ArrayList<Integer> matchColors = new ArrayList<>();
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                if (baseSkin.getOpacity(x, y) != 0 && !matchColors.contains(baseSkin.getColor(x, y))) {
                    matchColors.add(baseSkin.getColor(x, y));
                }
            }
        }
        if (matchColors.size() == 0) {
            return null;
        } else {
            NativeImage texture = new NativeImage(64, 64, false);
            texture.copyFrom(baseSkin);
            for (int x = 0; x <= 63; x++) {
                for (int y = 0; y <= 63; y++) {
                    if (!matchColors.contains(baseSkin.getColor(x, y))) {
                        texture.setColor(x, y, 0);
                    }
                }
            }
            return texture;
        }

    }
}
