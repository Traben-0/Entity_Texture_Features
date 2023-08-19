package traben.entity_texture_features.texture_handlers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteLoader;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class ETFSprite {

    private final Sprite sprite;
    //private final Sprite selfVariant;
    private final Sprite emissiveSprite;




    public final boolean isETFAltered;
    public ETFSprite(@NotNull Sprite originalSprite, @NotNull ETFTexture etfTexture){


        // the component ETFTexture at-least confirms the existence of the other component textures of the sprite atlas
        // we can use the atlas source to get these sprites for this object to hold
        // the ETFTexture already has all the checks for emissive and variant


        if(etfTexture.getVariantNumber() != 0){
            Identifier variantId = etfTexture.getTextureIdentifier(null);
            Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(variantId);
            if(resource.isPresent()){
                Sprite possibleVariant = null;
                try (SpriteContents contents = SpriteLoader.load(new Identifier(variantId +"-etf_sprite"), resource.get())) {
                    if(contents != null)
                        possibleVariant = new Sprite(variantId,contents,contents.getWidth(),contents.getHeight(),0,0);

                }
                sprite = Objects.requireNonNullElse(possibleVariant, originalSprite);
            }else{
                sprite = originalSprite;
            }
        }else{
            sprite = originalSprite;
        }

        isETFAltered = !sprite.equals(originalSprite);


        Sprite possibleEmissive = null;
        if(etfTexture.eSuffix != null) {
            Identifier emissiveId = etfTexture.getEmissiveIdentifierOfCurrentState();
            if(emissiveId!= null) {
                Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(emissiveId);
                if (resource.isPresent()) {

                    try (SpriteContents contents = SpriteLoader.load(new Identifier(emissiveId + "-etf_sprite"), resource.get())) {
                        if (contents != null)
                            possibleEmissive = new Sprite(emissiveId, contents, contents.getWidth(), contents.getHeight(), 0, 0);
                    }
                }
            }
        }
        emissiveSprite = possibleEmissive;
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
