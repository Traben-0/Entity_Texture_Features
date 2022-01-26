package traben.entity_texture_features.client;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
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
    }


    default void resetSingleVisuals(UUID id){
       // System.out.println("Entity Texture Features - Checking mob for texture change");
        UUID_isRandom.remove(id);
        UUID_randomTexture.remove(id);
    }

}
