package traben.entity_texture_features.texture_handlers;

import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class ETFSprite {

    private final Sprite sprite;
    //private final Sprite selfVariant;
    private final Sprite emissiveSprite;
    public ETFSprite(@NotNull Sprite originalSprite, @NotNull ETFTexture atlasComponentTexture, @NotNull SpriteSource source){
        Identifier id = originalSprite.getContents().getId();

        // the component ETFTexture at-least confirms the existence of the other component textures of the sprite atlas
        // we can use the atlas source to get these sprites for this object to hold
        // the ETFTexture already has all the checks for emissive and variant

        if(atlasComponentTexture.getVariantNumber() != 0){
            //this is a variant try to find it in the atlas
            Identifier variantId = new Identifier(id.toString()+atlasComponentTexture.getVariantNumber());
            Sprite possibleVariant = source.getSprite(variantId);
            if(!MissingSprite.getMissingSpriteId().equals(possibleVariant.getContents().getId())){
                sprite = possibleVariant;
                id = variantId;
            }else{
                sprite = originalSprite;
            }
        }else{
            sprite = originalSprite;
        }
        if(atlasComponentTexture.eSuffix != null){
            //this is emissive try to find it in the atlas
            emissiveSprite = source.getSprite(new Identifier(id.toString()+atlasComponentTexture.eSuffix));
        }else{
            emissiveSprite = source.getSprite(MissingSprite.getMissingSpriteId());
        }
    }

    @NotNull
    public Sprite getEmissive() {
        return emissiveSprite;
    }

    public boolean isEmissive() {
        return !MissingSprite.getMissingSpriteId().equals(emissiveSprite.getContents().getId());
    }

    @NotNull
    public Sprite getSpriteVariant() {
        return sprite;
        //return selfVariant == null ? self : selfVariant;
    }

    public interface SpriteSource{
        Sprite getSprite(Identifier id);
    }
}
