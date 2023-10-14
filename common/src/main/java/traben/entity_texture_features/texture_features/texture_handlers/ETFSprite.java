package traben.entity_texture_features.texture_features.texture_handlers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.resource.Resource;
import net.minecraft.resource.metadata.ResourceMetadata;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

public class ETFSprite {

    public final boolean isETFAltered;
    private final Sprite sprite;
    //private final Sprite selfVariant;
    private final Sprite emissiveSprite;

    public ETFSprite(@NotNull Sprite originalSprite, @NotNull ETFTexture etfTexture) {


        // the component ETFTexture at-least confirms the existence of the other component textures of the sprite atlas
        // we can use the atlas source to get these sprites for this object to hold
        // the ETFTexture already has all the checks for emissive and variant


        if (etfTexture.getVariantNumber() != 0) {
            Identifier variantId = etfTexture.getTextureIdentifier(null);
            Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(variantId);
            if (resource.isPresent()) {
                Sprite possibleVariant = null;

                try (SpriteContents contents = load(new Identifier(variantId + "-etf_sprite"), resource.get())) {
                    if (contents != null)
                        possibleVariant = new Sprite(variantId, contents, contents.getWidth(), contents.getHeight(), 0, 0);

                }
                sprite = Objects.requireNonNullElse(possibleVariant, originalSprite);
            } else {
                sprite = originalSprite;
            }
        } else {
            sprite = originalSprite;
        }

        isETFAltered = !sprite.equals(originalSprite);


        Sprite possibleEmissive = null;
        if (etfTexture.eSuffix != null) {
            Identifier emissiveId = etfTexture.getEmissiveIdentifierOfCurrentState();
            if (emissiveId != null) {
                Optional<Resource> resource = MinecraftClient.getInstance().getResourceManager().getResource(emissiveId);
                if (resource.isPresent()) {

                    try (SpriteContents contents = load(new Identifier(emissiveId + "-etf_sprite"), resource.get())) {
                        if (contents != null)
                            possibleEmissive = new Sprite(emissiveId, contents, contents.getWidth(), contents.getHeight(), 0, 0);
                    }
                }
            }
        }
        emissiveSprite = possibleEmissive;
    }

    @Nullable
    public static SpriteContents load(Identifier id, Resource resource) {
        ResourceMetadata animationResourceMetadata;
        try {
            //animationResourceMetadata = resource.getMetadata().decode(AnimationResourceMetadata.READER).orElse(AnimationResourceMetadata.EMPTY);
            animationResourceMetadata = resource.getMetadata();
        } catch (Exception var8) {
//            LOGGER.error("Unable to parse metadata from {}", id, var8);
            return null;
        }

        NativeImage nativeImage;
        try {
            InputStream inputStream = resource.getInputStream();

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
        SpriteDimensions spriteDimensions = new SpriteDimensions(nativeImage.getWidth(), nativeImage.getHeight());
        if (MathHelper.isMultipleOf(nativeImage.getWidth(), spriteDimensions.width()) && MathHelper.isMultipleOf(nativeImage.getHeight(), spriteDimensions.height())) {
            return new SpriteContents(id, spriteDimensions, nativeImage, animationResourceMetadata);
        } else {
//            LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", new Object[]{id, nativeImage.getWidth(), nativeImage.getHeight(), spriteDimensions.width(), spriteDimensions.height()});
            nativeImage.close();
            return null;
        }
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
