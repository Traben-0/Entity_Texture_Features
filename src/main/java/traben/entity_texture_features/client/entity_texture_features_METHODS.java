package traben.entity_texture_features.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.*;

public interface entity_texture_features_METHODS {



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
        System.out.println("Entity Texture Features - Reloading... (this may change all random mobs)");
        Texture_TotalTrueRandom.clear();
        UUID_randomTextureSuffix.clear();
        Texture_OptifineRandomSettingsPerTexture.clear();
        Texture_OptifineOrTrueRandom.clear();
        optifineOldOrVanilla.clear() ;// 0,1,2
        ignoreOnePNG.clear() ;
        UUID_entityAlreadyCalculated.clear();//only time it clears
        UUID_entityAwaitingDataClearing.clear();

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
             //ignoreOnePNG.put(vanillaTexturePath, !(isExistingFile(new Identifier( properties.replace(".properties","1.png")))));

             String propertiesRead = readProperties(properties);
             String[] perLine = propertiesRead.split("\n");
             ArrayList<String[]> eachCaseData = new ArrayList<>();
             int count = 1;
             ArrayList<String> maker = new ArrayList<>();
             for (String line:
                  perLine) {
                 line = line.trim();
                 //ignore blank lines or comments
                 if (!line.isBlank() && !line.startsWith("#")) {
                     if (!line.contains("." + count + "=")) {
                         if (!maker.isEmpty()) {
                             eachCaseData.add(maker.toArray(new String[0]));
                             maker.clear();
                         }
                         count++;
                     }
                     if (line.contains("." + count + "=")) {
                         maker.add(line);
                     } else if(!line.isBlank()){
                         System.out.println("Entity Texture Features - counting optifine properties failed");
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
                     dataAny[1] = dataAny[1].trim();
                     if (dataAny[0].contains("skins.") || dataAny[0].contains("textures.") ){
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

                     }else if (dataAny[0].contains("weights")){
                         String[] weightData = dataAny[1].split(" ");
                         ArrayList<Integer> builder = new ArrayList<>();
                         for (String s:
                              weightData) {
                             builder.add(Integer.parseInt(s.replaceAll("[^0-9]", "")));
                         }
                          weights = builder.toArray(new Integer[0]);
                     }else if (dataAny[0].contains("biomes")){
                         biomes = dataAny[1].toLowerCase().split(" ");
                     }else if (dataAny[0].contains("heights")){
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
                     }else if (dataAny[0].contains("name")){
                         if (dataAny[1].contains("regex:") || dataAny[1].contains("pattern:")){
                            names = new String[]{dataAny[1]};
                         }else {
                             names = dataAny[1].split(" ");
                         }
                     }else if (dataAny[0].contains("professions")){
                             professions = dataAny[1].split(" ");
                     }else if (dataAny[0].contains("collarColors") || dataAny[0].contains("collarColours")){
                         collarColours = dataAny[1].split(" ");
                     }else if (dataAny[0].contains("baby")){
                         switch (dataAny[1]) {
                             case "true" -> baby = 1;
                             case "false" -> baby = 2;
                         }
                     }else if (dataAny[0].contains("weather")){
                         switch (dataAny[1]) {
                             case "clear" -> weather = 1;
                             case "rain" -> weather = 2;
                             case "thunder" -> weather = 3;
                         }
                     }else if (dataAny[0].contains("health")){
                         health = dataAny[1].split(" ");
                     }else if (dataAny[0].contains("moonPhase")){
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
                     }else if (dataAny[0].contains("dayTime")){
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
                 System.out.println("Entity Texture Features - reading optifine properties file failed: "+properties);
             }

         }catch (Exception e){
             //System.out.println(e);
             System.out.println("Entity Texture Features - optifine properties failed to load: "+properties);
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
                System.out.println("Entity Texture Features - optifine properties failed to load: Texture heights range has a problem in properties file. this has occurred for value \""+rawRange.replace("N","-")+"\"");
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
            successCount++;
        } else if (isExistingFile( new Identifier(checkPathOldRandomFormat))) {
            optifineOldOrVanilla.put( vanillaPath,1);
            ignoreOnePNG.put( vanillaPath,false);
            successCount++;
        }else if (isExistingFile( new Identifier(checkPath))) {
            optifineOldOrVanilla.put( vanillaPath,2);
            ignoreOnePNG.put( vanillaPath,false);
            successCount++;
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
            successCount++;
        }else if (isExistingFile( new Identifier(checkPathOldRandomFormat))) {
            optifineOldOrVanilla.put( vanillaPath,1);
            keepGoing = true;
            successCount++;
        }else if (isExistingFile( new Identifier(checkPath))) {
            optifineOldOrVanilla.put( vanillaPath,2);
            keepGoing = true;
            successCount++;
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
                if (suffix == null){
                    suffix = readProperties("textures/emissive.properties");
                }
                String[] lines = suffix.split("\n");
                boolean alreadyPreferredEntityOption = false;
                for (String line:
                     lines) {
                    line = line.trim();
                    if (line.contains("entities.suffix.emissive")){
                        line = line.replace("entities.suffix.emissive","")
                            .replace("=","")
                            .replace(" ","");
                        emissiveSuffix = line;
                        alreadyPreferredEntityOption=true;
                        System.out.println("Entity Texture Features - Custom emissive suffix '"+emissiveSuffix+"' used" );
                    }else if (line.contains("suffix.emissive")){
                        line = line.replace("suffix.emissive","")
                                .replace("=","")
                                .replace(" ","");
                        if (!alreadyPreferredEntityOption) {
                            emissiveSuffix = line;
                            System.out.println("Entity Texture Features - Custom emissive suffix '" + emissiveSuffix + "' used");
                        }
                    }
                }
                if (emissiveSuffix == null) {
                    System.out.println("Entity Texture Features - Default emissive suffix '_e' used");
                    emissiveSuffix = "_e";
                }
            } catch (Exception e) {
                System.out.println("Entity Texture Features - Default emissive suffix '_e' used");
                emissiveSuffix = "_e";
            }
    }
}
