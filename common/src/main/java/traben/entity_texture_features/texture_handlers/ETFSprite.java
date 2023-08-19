package traben.entity_texture_features.texture_handlers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import traben.entity_texture_features.utils.ETFUtils2;

import java.util.Optional;

public class ETFSprite {

    private final Sprite sprite;
    //private final Sprite selfVariant;
    private final Sprite emissiveSprite;




    public final boolean isETFAltered;
    public ETFSprite(@NotNull Sprite originalSprite, @NotNull ETFTexture etfTexture){
        Sprite sprite1;


        // the component ETFTexture at-least confirms the existence of the other component textures of the sprite atlas
        // we can use the atlas source to get these sprites for this object to hold
        // the ETFTexture already has all the checks for emissive and variant


        if(etfTexture.getVariantNumber() != 0){
            Identifier variantId = etfTexture.getTextureIdentifier(null);
            Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(variantId);

            if(resource.isPresent()){
                try {
                    NativeImage image = ETFUtils2.getNativeImageElseNull(variantId);
                    Optional<AnimationResourceMetadata> meta = resource.get().getMetadata().decode(AnimationResourceMetadata.READER);
                    if(image != null) {
                        AnimationResourceMetadata anim = meta.orElse(AnimationResourceMetadata.EMPTY);

                        Sprite.Info info = new Sprite.Info(new Identifier(variantId + "-etf_sprite"),anim.getWidth(16), anim.getHeight(16), anim);

                        sprite1 = new Sprite(new SpriteAtlasTexture(variantId), info, 1, info.getWidth(), info.getHeight(), 0, 0,image);
                    }else{
                        sprite1 = originalSprite;
                    }
                }catch(Exception e){
                    sprite1 = originalSprite;
                }
            }else{
                sprite1 = originalSprite;
            }
        }else{
            sprite1 = originalSprite;
        }

        sprite = sprite1;
        isETFAltered = !sprite.equals(originalSprite);

        Sprite possibleEmissive = null;
        if(etfTexture.eSuffix != null) {
            Identifier emissiveId = etfTexture.getEmissiveIdentifierOfCurrentState();
            if(emissiveId!= null) {
                Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(emissiveId);
                if (resource.isPresent()) {
                    try {
                        NativeImage image = ETFUtils2.getNativeImageElseNull(emissiveId);
                        Optional<AnimationResourceMetadata> meta = resource.get().getMetadata().decode(AnimationResourceMetadata.READER);
                        if(image != null) {
                            AnimationResourceMetadata anim = meta.orElse(AnimationResourceMetadata.EMPTY);

                            Sprite.Info info = new Sprite.Info(new Identifier(emissiveId + "-etf_sprite"),anim.getWidth(16), anim.getHeight(16), anim);



                            possibleEmissive = new Sprite(new SpriteAtlasTexture(emissiveId), info, 1, info.getWidth(), info.getHeight(), 0, 0,image);
                        }
                    }catch(Exception e){
                    }
                }
            }
        }
        emissiveSprite = possibleEmissive;

        System.out.println("discovered painting texture: "+ etfTexture.thisIdentifier +", isVariant="+isETFAltered+", isEmissive="+isEmissive());
    }

    @NotNull
    public Sprite getEmissive() {
        return emissiveSprite;
    }

    public boolean isEmissive() {
        return emissiveSprite != null;
    }

    @NotNull
    public Sprite getSpriteVariant() {
        return sprite;
    }


}
