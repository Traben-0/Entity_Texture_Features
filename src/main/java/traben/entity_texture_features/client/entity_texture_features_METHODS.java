package traben.entity_texture_features.client;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;

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

}
