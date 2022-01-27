package traben.entity_texture_features.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
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


    default void resetSingleVisuals(UUID id){
       // System.out.println("Entity Texture Features - Checking mob for texture change");
        UUID_isRandom.remove(id);
        UUID_randomTexture.remove(id);
    }

    default String readProperties(Resource resource){
         try {
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

    default void setEmissiveSuffix(){
        try {
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("optifine/emissive.properties"));
            try {
                String suffix = readProperties(resource);
                resource.close();
                if (suffix == null){
                    Resource resource2 = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("textures/emissive.properties"));
                    suffix = readProperties(resource2);
                    resource2.close();
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
                resource.close();
                System.out.println("Entity Texture Features - Default emissive suffix '_e' used");
                emissiveSuffix = "_e";
            }
        }catch (Exception f) {
            System.out.println("Entity Texture Features - Default emissive suffix '_e' used");
            emissiveSuffix = "_e";
        }
    }
}
