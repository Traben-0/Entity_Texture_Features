package traben.entity_texture_features.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

public interface ETF_METHODS {



     default boolean isExistingFile( Identifier id) {
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
        }catch (IOException f) {
            //System.out.println("2="+id.getPath()+f);
            return false;
        }
    }

    default void resetVisuals(){
        modMessage("Reloading... (this may change all random mobs)",false);
        Texture_TotalTrueRandom.clear();
        UUID_randomTextureSuffix.clear();
        Texture_OptifineRandomSettingsPerTexture.clear();
        Texture_OptifineOrTrueRandom.clear();
        optifineOldOrVanilla.clear() ;// 0,1,2
        ignoreOnePNG.clear() ;
        UUID_entityAlreadyCalculated.clear();//only time it clears
        UUID_entityAwaitingDataClearing.clear();

        UUID_playerHasFeatures.clear();
        UUID_playerHasEnchant.clear();
        UUID_playerHasEmissive.clear();
        UUID_playerTransparentSkinId.clear();
        UUID_playerSkinDownloadedYet.clear();
        for (HttpURLConnection h:
             UUID_HTTPtoDisconnect.values()) {
                h.disconnect();
        }
        UUID_HTTPtoDisconnect.clear();

        UUID_HasBlink.clear();
        UUID_HasBlink2.clear();

        UUID_TridentName.clear();

        Texture_Emissive.clear();
        setEmissiveSuffix();
    }

    default void resetSingleData(UUID id){
        UUID_randomTextureSuffix.remove(id);
    }


    default String readProperties(String path){
         try {
             Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(path));
             try {
                 InputStream in = resource.getInputStream();
                 StringBuilder str = new StringBuilder();
                 int content;
                 while ((content = in.read()) != -1) {
                     str.append((char) content);
                 }
                 resource.close();
                 return str.toString();

             } catch (Exception e) {
                 resource.close();
                 return "";
             }
         }catch (Exception e) {
             return "";
         }
    }

    default boolean checkPropertiesExist(String path){
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
        }catch (Exception e) {
            return false;
        }
    }

    default void processNewRandomTextureCandidate(String vanillaTexturePath){
        boolean hasProperties = false;
        //gonna ignore this for now
        //if (vanillaTexturePath.contains("wolf")){
        //    vanillaTexturePath = vanillaTexturePath.replace("wolf_angry","wolf")
        //    vanillaTexturePath = vanillaTexturePath.replace("wolf_tame","wolf")
        //}
        String properties="";
        if (checkPropertiesExist(vanillaTexturePath.replace(".png", ".properties").replace("textures", "optifine/random"))){
            properties = vanillaTexturePath.replace(".png", ".properties").replace("textures", "optifine/random");
            hasProperties = true;
            optifineOldOrVanilla.put(vanillaTexturePath,0);
        }else if (isExistingFile(new Identifier( vanillaTexturePath.replace(".png", "2.png").replace("textures", "optifine/random")))){
            optifineOldOrVanilla.put(vanillaTexturePath,0);
        }else if (checkPropertiesExist(vanillaTexturePath.replace(".png", ".properties").replace("textures/entity", "optifine/mob"))){
            properties =vanillaTexturePath.replace(".png", ".properties").replace("textures/entity", "optifine/mob");
            hasProperties = true;
            optifineOldOrVanilla.put(vanillaTexturePath,1);
        }else if ( isExistingFile(new Identifier( vanillaTexturePath.replace(".png", "2.png").replace("textures/entity", "optifine/mob")))){
            optifineOldOrVanilla.put(vanillaTexturePath,1);
        }else if (checkPropertiesExist(vanillaTexturePath.replace(".png", ".properties"))){
            properties =vanillaTexturePath.replace(".png", ".properties");
            hasProperties = true;
            optifineOldOrVanilla.put(vanillaTexturePath,2);
        }else if ( isExistingFile(new Identifier( vanillaTexturePath.replace(".png", "2.png")))){
            optifineOldOrVanilla.put(vanillaTexturePath,2);
        }

         //no settings just true random
         if (hasProperties){//optifine settings found
             processOptifineTextureCandidate(vanillaTexturePath, properties);
         }else{
             processTrueRandomCandidate(vanillaTexturePath);
         }
    }

    private void processOptifineTextureCandidate(String vanillaTexturePath, String properties){
         try {
             ignoreOnePNG.put(vanillaTexturePath, !(isExistingFile(new Identifier( properties.replace(".properties","1.png")))));

             String propertiesRead = readProperties(properties);
             String[] perLine = propertiesRead.split("\n");
             ArrayList<String[]> eachCaseData = new ArrayList<>();
             int count = 1;
             ArrayList<String> maker = new ArrayList<>();
             for (String line:
                  perLine) {
                 line = line.trim();
                 //ignore blank lines or comments
                 if (!line.isBlank() && !line.startsWith("#") && line.contains("=") ) {
                     if (!line.contains("." + count + "=")) {
                         if (!maker.isEmpty()) {
                             eachCaseData.add(maker.toArray(new String[0]));
                             maker.clear();
                         }
                         //count++;
                         //set count value from read data for people who write properties and can't count
                         count = Integer.parseInt(line.split("=")[0].split("\\.")[1].replaceAll("[^0-9]", ""));

                     }
                     if (line.contains("." + count + "=")) {
                         maker.add(line);
                     } else if(!line.isBlank()){
                         modMessage("counting optifine properties failed",false);
                     }
                 }
             }//add last one if lines ended
             if (!maker.isEmpty()) {
                 eachCaseData.add(maker.toArray(new String[0]));
                 maker.clear();
             }
             //from here each case data can build cases
             ArrayList<randomCase> allCasesForTexture = new ArrayList<>();
             for (String[] caseStrings:
                  eachCaseData) {
                 Integer[] suffixes= {};
                 Integer[] weights={};
                 String[] biomes={};
                 Integer[] heights={};
                 String[] names={};
                 String[] professions= {};
                 String[] collarColours= {};
                  int baby= 0; // 0 1 2 - dont true false
                  int weather = 0; //0,1,2,3 - no clear rain thunder
                  String[] health= {};
                  Integer[] moon= {};
                  String[] daytime= {};
                 for (String caseLine:
                      caseStrings) {
                     //here every line is data "skins.1=2-4" for all of ".1=" case
                     String[] dataAny = caseLine.split("=");
                     if (!dataAny[1].contains("regex") && !dataAny[1].contains("pattern")) {
                         dataAny[1] = dataAny[1].trim().replaceAll("\\s+", " ");
                     }else{
                         dataAny[1] = dataAny[1].trim();
                     }
                     if (dataAny[0].startsWith("skins.") || dataAny[0].startsWith("textures.") ){
                        String[] skinData = dataAny[1].split(" ");
                        ArrayList<Integer> suffixNumbers = new ArrayList<>();
                         for (String data:
                              skinData) {
                             //check if range
                             if (data.contains("-")){
                                 suffixNumbers.addAll(Arrays.asList(getIntRange(data)));
                             }else {
                                 suffixNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                             }
                         }
                        suffixes = suffixNumbers.toArray(new Integer[0]);

                     }else if (dataAny[0].startsWith("weights")){
                         String[] weightData = dataAny[1].split(" ");
                         ArrayList<Integer> builder = new ArrayList<>();
                         for (String s:
                              weightData) {
                             builder.add(Integer.parseInt(s.replaceAll("[^0-9]", "")));
                         }
                          weights = builder.toArray(new Integer[0]);
                     }else if (dataAny[0].startsWith("biomes")){
                         biomes = dataAny[1].toLowerCase().split(" ");
                     }else if (dataAny[0].startsWith("heights")){
                         String[] heightData = dataAny[1].split(" ");
                         ArrayList<Integer> heightNumbers = new ArrayList<>();
                         for (String data:
                                 heightData) {
                             //check if range
                             if (data.contains("-")){
                                 heightNumbers.addAll(Arrays.asList(getIntRange(data)));
                             }else{
                                 heightNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                             }
                         }
                         heights = heightNumbers.toArray(new Integer[0]);
                     }else if (dataAny[0].startsWith("name")){
                         if (dataAny[1].contains("regex:") || dataAny[1].contains("pattern:")){
                            names = new String[]{dataAny[1]};
                         }else {
                             names = dataAny[1].split(" ");
                         }
                     }else if (dataAny[0].startsWith("professions")){
                             professions = dataAny[1].split(" ");
                     }else if (dataAny[0].startsWith("collarColors") || dataAny[0].startsWith("collarColours")){
                         collarColours = dataAny[1].split(" ");
                     }else if (dataAny[0].startsWith("baby")){
                         switch (dataAny[1]) {
                             case "true" -> baby = 1;
                             case "false" -> baby = 2;
                         }
                     }else if (dataAny[0].startsWith("weather")){
                         switch (dataAny[1]) {
                             case "clear" -> weather = 1;
                             case "rain" -> weather = 2;
                             case "thunder" -> weather = 3;
                         }
                     }else if (dataAny[0].startsWith("health")){
                         health = dataAny[1].split(" ");
                     }else if (dataAny[0].startsWith("moonPhase")){
                         String[] moonData = dataAny[1].split(" ");
                         ArrayList<Integer> moonNumbers = new ArrayList<>();
                         for (String data:
                                 moonData) {
                             //check if range
                             if (data.contains("-")){
                                 moonNumbers.addAll(Arrays.asList(getIntRange(data)));
                             }else {
                                 moonNumbers.add(Integer.parseInt(data.replaceAll("[^0-9]", "")));
                             }
                         }
                         moon = moonNumbers.toArray(new Integer[0]);
                     }else if (dataAny[0].startsWith("dayTime")){
                         daytime = dataAny[1].split(" ");
                     }
                 }
                 //here must create case
                 if (suffixes.length!=0){
                    allCasesForTexture.add( new randomCase(suffixes, weights, biomes, heights, names,professions,collarColours,baby,weather,health,moon,daytime));
                 }
             }
             //done
             if (!allCasesForTexture.isEmpty()) {
                 Texture_OptifineRandomSettingsPerTexture.put(vanillaTexturePath,allCasesForTexture);
                 Texture_OptifineOrTrueRandom.put(vanillaTexturePath, true);
             }else{
                 modMessage("Entity Texture Features - reading optifine properties file failed: "+properties,false);
             }

         }catch (Exception e){
             //System.out.println(e);
             modMessage("Entity Texture Features - optifine properties failed to load: @("+properties+") Error:"+e,false);
         }
    }

    default Integer[] getIntRange(String rawRange){
         //assume rawRange =  "20-56"  but can be "-64-56"  or "-14"
        rawRange = rawRange.trim();
        //sort negatives before split
        if(rawRange.startsWith("-")){
            rawRange = rawRange.replaceFirst("-","N");
        }
        rawRange =rawRange.replaceAll("--","-N");
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
            if (minMax[0] > minMax[1]){
                //0 must be smaller
                minMax = new int[] {minMax[1],minMax[0]};
            }
            if (minMax[0] < minMax[1]) {
                for (int i = minMax[0]; i <= minMax[1]; i++) {
                    builder.add(i);
                }
            }else{
                modMessage("Optifine properties failed to load: Texture heights range has a problem in properties file. this has occurred for value \""+rawRange.replace("N","-")+"\"",false);
            }
            return builder.toArray(new Integer[0]);
        }else{//only 1 number but method ran because of "-" present
            if (split[0].contains("N")) {
                return new Integer[] {-Integer.parseInt(split[0].replaceAll("[^0-9]", ""))};
            }else{
                return new Integer[] { Integer.parseInt(split[0].replaceAll("[^0-9]", ""))};
            }

        }
    }

    default void testCases(String vanillaPath,UUID id, Entity entity){
        for (randomCase test :
                Texture_OptifineRandomSettingsPerTexture.get(vanillaPath)) {
            if (test.testEntity((LivingEntity) entity, UUID_entityAlreadyCalculated.contains(id))) {
                UUID_randomTextureSuffix.put(id, test.getWeightedSuffix(id, ignoreOnePNG.get(vanillaPath)));
                Identifier tested = returnOptifineOrVanillaIdentifier(vanillaPath, UUID_randomTextureSuffix.get(id));
                if (!isExistingFile(tested)){
                    UUID_randomTextureSuffix.put(id, 0);
                }
                break;
            }
        }
        if (!hasUpdatableRandomCases.containsKey(id))
        hasUpdatableRandomCases.put(id ,false);
    }

    default void modMessage(String message,boolean inChat){
        //System.out.println("[Entity Texture Features]: "+message);
        LogManager.getLogger().info("[Entity Texture Features]: " + message);
    }

    default String returnOptifineOrVanillaPath(String vanillaPath, int randomId, String emissiveSuffx){
        return switch (optifineOldOrVanilla.get(vanillaPath)) {
            case 0 -> vanillaPath.replace(".png", randomId +emissiveSuffx+ ".png").replace("textures", "optifine/random");
            case 1 -> vanillaPath.replace(".png", randomId  +emissiveSuffx+ ".png").replace("textures/entity", "optifine/mob");
            default -> vanillaPath.replace(".png", randomId  +emissiveSuffx+ ".png");
        };
    }
    default Identifier returnOptifineOrVanillaIdentifier(String vanillaPath, int randomId, String emissiveSuffx){
         return new Identifier(returnOptifineOrVanillaPath(vanillaPath, randomId, emissiveSuffx));
    }
    default Identifier returnOptifineOrVanillaIdentifier(String vanillaPath, int randomId){
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
        if (isExistingFile( new Identifier(checkPathOptifineFormat))) {
            optifineOldOrVanilla.put( vanillaPath,0);
            ignoreOnePNG.put( vanillaPath,false);
            //successCount++;
        } else if (isExistingFile( new Identifier(checkPathOldRandomFormat))) {
            optifineOldOrVanilla.put( vanillaPath,1);
            ignoreOnePNG.put( vanillaPath,false);
            //successCount++;
        }else if (isExistingFile( new Identifier(checkPath))) {
            optifineOldOrVanilla.put( vanillaPath,2);
            ignoreOnePNG.put( vanillaPath,false);
            //successCount++;
        }else{
            ignoreOnePNG.put( vanillaPath,true);
        }

        //check if texture 2.png is used
        checkPath = vanillaPath.replace(".png", "2.png");
        checkPathOldRandomFormat = vanillaPath.replace(".png", "2.png").replace("textures/entity", "optifine/mob");
        checkPathOptifineFormat = vanillaPath.replace(".png", "2.png").replace("textures", "optifine/random");
        if (isExistingFile( new Identifier(checkPathOptifineFormat))) {
            optifineOldOrVanilla.put( vanillaPath,0);
            keepGoing = true;
            successCount=2;
        }else if (isExistingFile( new Identifier(checkPathOldRandomFormat))) {
            optifineOldOrVanilla.put( vanillaPath,1);
            keepGoing = true;
            successCount=2;
        }else if (isExistingFile( new Identifier(checkPath))) {
            optifineOldOrVanilla.put( vanillaPath,2);
            keepGoing = true;
            successCount=2;
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
            keepGoing = isExistingFile( new Identifier(checkPath));
            if (keepGoing) successCount++;
        }
        //true if any random textures at all

        Texture_TotalTrueRandom.put(vanillaPath, successCount);
        Texture_OptifineOrTrueRandom.put(vanillaPath, false);

    }


    default void setEmissiveSuffix(){


        try {
                String suffix = readProperties("optifine/emissive.properties");
                if (suffix == null || suffix.isBlank()){
                    suffix = readProperties("textures/emissive.properties");
                }
                String[] lines = suffix.split("\n");
                ArrayList<String> builder = new ArrayList<>();
                for (String line:
                     lines) {
                    line = line.trim();
                    if (line.contains("suffix.emissive")){
                        line = line.split("=")[1].trim();
                        builder.add(line);
                        modMessage("Custom emissive suffix '" + line + "' added",false);
                    }
                    if(ETFConfigData.alwaysCheckVanillaEmissiveSuffix
                            && !builder.contains("_e")){
                        builder.add("_e");
                    }
                }
                emissiveSuffix = builder.toArray(new String[0]);
                if (emissiveSuffix.length==0) {
                    modMessage("Error! Default emissive suffix '_e' used",false);
                    emissiveSuffix = new String[] {"_e"};
                }
            } catch (Exception e) {
                modMessage("Error! default emissive suffix '_e' used",false);
                emissiveSuffix = new String[] {"_e"};
            }
    }

    default void saveConfig() {
        File config = new File(FabricLoader.getInstance().getConfigDir().toFile(), "entity_texture_features.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!config.getParentFile().exists()) {
            config.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            fileWriter.write(gson.toJson(ETFConfigData));
            fileWriter.close();
        } catch (IOException e) {
            modMessage("Config could not be saved",false);
        }
    }
}
