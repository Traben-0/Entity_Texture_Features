package traben.entity_texture_features.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.*;

public interface entity_texture_features_METHODS {



     default boolean isExistingFile(ResourceManager resourceManager, Identifier id) {
        try {
            Resource resource = resourceManager.getResource(id);
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
        UUID_isRandom.clear();// = new HashMap<UUID, Integer[]>() ;
        Texture_Emissive.clear();// = new HashMap<String, Identifier>() ;
        UUID_randomTexture.clear();
        //emissiveSuffix = null;
        setEmissiveSuffix();
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
                 return null;
             }
         }catch (Exception e) {
             return null;
         }

    }

    default void processNewRandomTextureCandidate(String vanillaTexturePath, Entity entity){
         String possibleProperties = readProperties(vanillaTexturePath.replace(".png", ".properties").replace("textures/entity", "optifine/random"));
         if (possibleProperties == null){
             possibleProperties = readProperties(vanillaTexturePath.replace(".png", ".properties").replace("textures/entity", "optifine/mob"));
             if (possibleProperties == null){
                 possibleProperties = readProperties(vanillaTexturePath.replace(".png", ".properties"));
             }
         }//no settings just true random
         if (possibleProperties == null){
             processTrueRandomCandidate(vanillaTexturePath,entity);
         }else{//optifine settings found
             processOptifineTextureCandidate(vanillaTexturePath,entity,possibleProperties);
         }
    }

    private void processOptifineTextureCandidate(String vanillaTexturePath, Entity entity, String properties){
         try {
             properties = readProperties(properties);
             String[] perLine = properties.split("\n");
             ArrayList<String[]> eachCaseData = new ArrayList<String[]>();
             int count = 1;
             ArrayList<String> maker = new ArrayList<String>();
             for (String line:
                  perLine) {
                 if (!line.contains("."+count+"=")){
                     if (!maker.isEmpty()){
                         eachCaseData.add(maker.toArray(String[]::new));
                         maker.clear();
                     }
                     count++;
                 }
                 if (line.contains("."+count+"=")){
                     maker.add(line);
                 }else{
                     System.out.println("counting optifine properties failed");
                 }
             }
             //from here each case data can build cases
             for (String[] caseStrings:
                  eachCaseData) {
                 for (String caseLine:
                      caseStrings) {
                     //here every line is "data skins.1=2-4"
                        read here
                 }
                 //here must create case
                 randomCase newCase = new randomCase();
             }


         }catch (Exception e){
             System.out.println("optifine properties failed to load");
         }
    }

    private void processTrueRandomCandidate(String vanillaPath, Entity entity) {
         ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
         UUID id = entity.getUuid();
        boolean keepGoing = false;
        //ArrayList<String> allTextures = new ArrayList<String>();
        String checkPath;
        String checkPathOptifineFormat;
        String checkPathOldRandomFormat;
        //first iteration longer
        int successCount = 0;
        //allTextures.add(vanillaPath);
        //can start from either texture1.png or texture2.png check both first
        //check if texturename1.png is used
        checkPath = vanillaPath.replace(".png", "1.png");
        checkPathOldRandomFormat = vanillaPath.replace(".png", "1.png").replace("textures/entity", "optifine/mob");
        checkPathOptifineFormat = vanillaPath.replace(".png", "1.png").replace("textures", "optifine/random");
        if (isExistingFile(resourceManager, new Identifier(checkPathOptifineFormat))) {
            optifineOldOrVanilla.put( vanillaPath,0);
            ignoreOnePNG.put( vanillaPath,false);
            successCount++;
        } else if (isExistingFile(resourceManager, new Identifier(checkPathOldRandomFormat))) {
            optifineOldOrVanilla.put( vanillaPath,1);
            ignoreOnePNG.put( vanillaPath,false);
            successCount++;
        }else if (isExistingFile(resourceManager, new Identifier(checkPath))) {
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
        if (isExistingFile(resourceManager, new Identifier(checkPathOptifineFormat))) {
            optifineOldOrVanilla.put( vanillaPath,0);
            keepGoing = true;
            successCount++;
        }else if (isExistingFile(resourceManager, new Identifier(checkPathOldRandomFormat))) {
            optifineOldOrVanilla.put( vanillaPath,1);
            keepGoing = true;
            successCount++;
        }else if (isExistingFile(resourceManager, new Identifier(checkPath))) {
            optifineOldOrVanilla.put( vanillaPath,2);
            keepGoing = true;
            successCount++;
        }
        //texture3.png and further optimized iterations
        int count = 2;
        while (keepGoing) {
            count++;
            if (optifineOldOrVanilla.get(id) == 0) {
                checkPath = vanillaPath.replace(".png", (count + ".png")).replace("textures", "optifine/random");
                successCount++;
            } else if (optifineOldOrVanilla.get(id) == 1) {
                checkPath = vanillaPath.replace(".png", (count + ".png")).replace("textures/entity", "optifine/mob");
                successCount++;
            } else {
                checkPath = vanillaPath.replace(".png", (count + ".png"));
                successCount++;
            }
            keepGoing = isExistingFile(resourceManager, new Identifier(checkPath));
        }
        //true if any random textures at all

        Texture_TotalRandom.put(vanillaPath, successCount);

    }


    default void setEmissiveSuffix(){
            try {
                String suffix = readProperties("optifine/emissive.properties");
                if (suffix == null){
                    suffix = readProperties("textures/emissive.properties");
                }
                if (suffix.contains("suffix.emissive")){
                    suffix = suffix.replace("suffix.emissive","")
                            .replace("=","")
                            .replace(" ","");
                    emissiveSuffix = suffix;
                    System.out.println("Entity Texture Features - Custom emissive suffix '"+emissiveSuffix+"' used" );
                }else{
                    System.out.println("Entity Texture Features - Default emissive suffix '_e' used");
                    emissiveSuffix = "_e";
                }

            } catch (Exception e) {

                System.out.println("Entity Texture Features - Default emissive suffix '_e' used");
                emissiveSuffix = "_e";
            }
    }
}
