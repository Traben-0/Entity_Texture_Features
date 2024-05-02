package traben.entity_texture_features.features.texture_handlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;

public class ETFSprite {

    public final boolean isETFAltered;
    private final TextureAtlasSprite sprite;
    //private final Sprite selfVariant;
    private final TextureAtlasSprite emissiveSprite;

    public ETFSprite(@NotNull TextureAtlasSprite originalSprite, @NotNull ETFTexture etfTexture) {


        // the component ETFTexture at-least confirms the existence of the other component textures of the sprite atlas
        // we can use the atlas source to get these sprites for this object to hold
        // the ETFTexture already has all the checks for emissive and variant


        if (etfTexture.getVariantNumber() != 0) {
            ResourceLocation variantId = etfTexture.getTextureIdentifier(null);
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(variantId);
            if (resource.isPresent()) {
                TextureAtlasSprite possibleVariant = null;

                try (SpriteContents contents = load(new ResourceLocation(variantId + "-etf_sprite"), resource.get())) {
                    if (contents != null)
                        possibleVariant = new TextureAtlasSprite(variantId, contents, contents.width(), contents.height(), 0, 0);

                }
                sprite = Objects.requireNonNullElse(possibleVariant, originalSprite);
            } else {
                sprite = originalSprite;
            }
        } else {
            sprite = originalSprite;
        }

        isETFAltered = !sprite.equals(originalSprite);


        TextureAtlasSprite possibleEmissive = null;
        if (etfTexture.eSuffix != null) {
            ResourceLocation emissiveId = etfTexture.getEmissiveIdentifierOfCurrentState();
            if (emissiveId != null) {
                Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(emissiveId);
                if (resource.isPresent()) {

                    try (SpriteContents contents = load(new ResourceLocation(emissiveId + "-etf_sprite"), resource.get())) {
                        if (contents != null)
                            possibleEmissive = new TextureAtlasSprite(emissiveId, contents, contents.width(), contents.height(), 0, 0);
                    }
                }
            }
        }
        emissiveSprite = possibleEmissive;
    }

    @Nullable
    public static SpriteContents load(ResourceLocation id, Resource resource) {
        ResourceMetadata animationResourceMetadata;
        try {
            //animationResourceMetadata = resource.getMetadata().decode(AnimationResourceMetadata.READER).orElse(AnimationResourceMetadata.EMPTY);
            animationResourceMetadata = resource.metadata();
        } catch (Exception var8) {
//            LOGGER.error("Unable to parse metadata from {}", id, var8);
            return null;
        }

        NativeImage nativeImage;
        try {
            InputStream inputStream = resource.open();

            try {
                nativeImage = NativeImage.read(inputStream);
            } catch (Throwable var9) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable var7) {
                        var9.addSuppressed(var7);
                    }
                }

                throw var9;
            }

            //if (inputStream != null) {
            inputStream.close();
            //}
        } catch (IOException var10) {
//            LOGGER.error("Using missing texture, unable to load {}", id, var10);
            return null;
        }

        //SpriteDimensions spriteDimensions = animationResourceMetadata.getSize(nativeImage.getWidth(), nativeImage.getHeight());
        FrameSize spriteDimensions = new FrameSize(nativeImage.getWidth(), nativeImage.getHeight());
        if (Mth.isMultipleOf(nativeImage.getWidth(), spriteDimensions.width()) && Mth.isMultipleOf(nativeImage.getHeight(), spriteDimensions.height())) {
            return new SpriteContents(id, spriteDimensions, nativeImage, animationResourceMetadata);
        } else {
//            LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", new Object[]{id, nativeImage.getWidth(), nativeImage.getHeight(), spriteDimensions.width(), spriteDimensions.height()});
            nativeImage.close();
            return null;
        }
    }

    @NotNull
    public TextureAtlasSprite getEmissive() {
        return emissiveSprite;
    }

    public boolean isEmissive() {
        return emissiveSprite != null;
    }

    @NotNull
    public TextureAtlasSprite getSpriteVariant() {
        return sprite;
    }

}
