package traben.entity_texture_features.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;


public abstract class ETFUtils2 {


    public static Identifier getETFVariantNotNullForInjector(Identifier identifier) {
        //do not modify texture
        if (ETFRenderContext.getCurrentEntity() == null
                || !ETFRenderContext.isAllowedToRenderLayerTextureModify())
            return identifier;

        //get etf modified texture
        ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(identifier, ETFRenderContext.getCurrentEntity());
        if (ETFRenderContext.isAllowedToPatch()) {
            etfTexture.assertPatchedTextures();
        }
        Identifier modified = etfTexture.getTextureIdentifier(ETFRenderContext.getCurrentEntity());

        //check not null just to be safe, it shouldn't be however
        //noinspection ConstantValue
        return modified == null ? identifier : modified;
    }

    public static boolean renderEmissive(ETFTexture texture, VertexConsumerProvider provider, RenderMethodForOverlay renderer) {
        if (!ETF.config().getConfig().canDoEmissiveTextures()) return false;
        Identifier emissive = texture.getEmissiveIdentifierOfCurrentState();
        if (emissive != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();

            VertexConsumer emissiveConsumer = provider.getBuffer(
                    ETFRenderContext.canRenderInBrightMode() ?
                            RenderLayer.getBeaconBeam(emissive, true) :
                            ETFRenderContext.shouldEmissiveUseCullingLayer() ?
                                    RenderLayer.getEntityTranslucentCull(emissive) :
                                    RenderLayer.getEntityTranslucent(emissive));

            if (wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();

            ETFRenderContext.startSpecialRenderOverlayPhase();
            renderer.render(emissiveConsumer, ETF.EMISSIVE_FEATURE_LIGHT_VALUE);
            ETFRenderContext.endSpecialRenderOverlayPhase();
            return true;
        }
        return false;
    }

    public static boolean renderEnchanted(ETFTexture texture, VertexConsumerProvider provider, int light, RenderMethodForOverlay renderer) {
        //attempt enchanted render
        Identifier enchanted = texture.getEnchantIdentifierOfCurrentState();
        if (enchanted != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();
            VertexConsumer enchantedVertex = ItemRenderer.getArmorGlintConsumer(provider, RenderLayer.getArmorCutoutNoCull(enchanted), false, true);
            if (wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();

            ETFRenderContext.startSpecialRenderOverlayPhase();
            renderer.render(enchantedVertex, light);
            ETFRenderContext.endSpecialRenderOverlayPhase();
            return true;
        }
        return false;
    }

    @NotNull
    public static Identifier addVariantNumberSuffix(Identifier identifier, int variant) {
        return new Identifier(addVariantNumberSuffix(identifier.toString(), variant));
    }

    @NotNull
    public static String addVariantNumberSuffix(String identifierString, int variant) {
        String file;
        if (identifierString.endsWith(".png")) {
            file = "png";
        } else {
            String[] split = identifierString.split("\\.");
            file = split[split.length - 1];
        }
        if (variant < 2)
            return identifierString;
        if (identifierString.matches("\\D+\\d+\\." + file)) {
            return identifierString.replace("." + file, "." + variant + "." + file);
        }
        return identifierString.replace("." + file, variant + "." + file);
    }

    @Nullable
    public static Identifier replaceIdentifier(Identifier id, String regex, String replace) {
        if (id == null) return null;
        Identifier forReturn;
        try {
            forReturn = new Identifier(id.getNamespace(), id.getPath().replaceFirst(regex, replace));
        } catch (InvalidIdentifierException idFail) {
            ETFUtils2.logError(ETFVersionDifferenceHandler.getTextFromTranslation("config.entity_texture_features.illegal_path_recommendation").getString() + "\n" + idFail);
            forReturn = null;
        } catch (Exception e) {
            forReturn = null;
        }
        return forReturn;
    }

    @Nullable
    public static String returnNameOfHighestPackFromTheseMultiple(String[] packNameList) {
        ArrayList<String> packNames = new ArrayList<>(Arrays.asList(packNameList));
        //loop through and remove the one from the lowest pack of the first 2 entries
        //this iterates over the whole array
        while (packNames.size() >= 2) {
            if (ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(packNames.get(0)) >= ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(packNames.get(1))) {
                packNames.remove(1);
            } else {
                packNames.remove(0);
            }
        }
        //here the array is down to 1 entry which should be the one in the highest pack
        return packNames.get(0);
    }

    @Nullable
    public static String returnNameOfHighestPackFromTheseTwo(String pack1, String pack2) {
        if (pack1 == null) return null;

        if (pack1.equals(pack2)) {
            return pack1;
        }
        if (ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(pack1)
                >= ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(pack2)) {
            return pack1;
        } else {
            return pack2;
        }
    }

//    @SuppressWarnings("BooleanMethodIsAlwaysInverted") //makes more logical sense
//    public static boolean isNativeImageEmpty(@NotNull NativeImage image) {
//        boolean foundNonEmptyPixel = false;
//        for (int x = 0; x < image.getWidth(); x++) {
//            for (int y = 0; y < image.getHeight(); y++) {
//                if (image.getColor(x, y) != 0) {
//                    foundNonEmptyPixel = true;
//                    break;
//                }
//            }
//            if (foundNonEmptyPixel) break;
//        }
//        return !foundNonEmptyPixel;
//    }

    @Nullable
    public static Properties readAndReturnPropertiesElseNull(Identifier path) {
        Properties props = new Properties();
        try {

            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(path);
            try {
                InputStream in = resource.getInputStream();
                props.load(in);
                in.close();
                return props;
            } catch (Exception e) {
                //resource.close();
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }

    public static NativeImage getNativeImageElseNull(@Nullable Identifier identifier) {
        if(identifier == null) return null;
//        if (identifier != null) {
//            NativeImage image = ETFManager.getInstance().KNOWN_NATIVE_IMAGES.get(identifier);
//            if (image != null) {
//                return image;
//            }
//        }
        NativeImage img;
        try{
        try {
            //try catch is intended
            //noinspection OptionalGetWithoutIsPresent
            InputStream in = MinecraftClient.getInstance().getResourceManager().getResource(identifier).getInputStream();
            try {
                img = NativeImage.read(in);
                in.close();
//                KNOWN_NATIVE_IMAGES.put(identifier, img);
                return img;
            } catch (Exception e) {
                //resource.close();
                in.close();
                return null;
            }
        } catch (Exception e) {
            AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(identifier);
            if (texture instanceof NativeImageBackedTexture nativeImageBackedTexture) {
                var image2 = nativeImageBackedTexture.getImage();
                if(image2 == null) return null;
                NativeImage image3 = new NativeImage(image2.getWidth(), image2.getHeight(), false);
                image3.copyFrom(image2);
                return image3;
            }
            return null;
        }
        } catch (Exception e) {

                return null;
        }

    }

    //improvements to logging by @Maximum#8760
    public static void logMessage(String obj) {
        logMessage(obj, false);
    }

    public static void logMessage(String obj, boolean inChat) {
        if (!obj.endsWith(".")) obj = obj + ".";
        if (inChat) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.of("§a[INFO]§r [Entity Texture Features]: " + obj), false);
            } else {
                ETF.LOGGER.info(obj);
            }
        } else {
            ETF.LOGGER.info(obj);
        }
    }

    //improvements to logging by @Maximum#8760
    public static void logWarn(String obj) {
        logWarn(obj, false);
    }

    public static void logWarn(String obj, boolean inChat) {
        if (!obj.endsWith(".")) obj = obj + ".";
        if (inChat) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.of("§e[WARN]§r [Entity Texture Features]: " + obj), false);
            } else {
                ETF.LOGGER.warn(obj);
            }
        } else {
            ETF.LOGGER.warn(obj);
        }
    }

    //improvements to logging by @Maximum#8760
    public static void logError(String obj) {
        logError(obj, false);
    }

    public static void logError(String obj, boolean inChat) {
        if (!obj.endsWith(".")) obj = obj + ".";
        if (inChat) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                player.sendMessage(Text.of("§4[ERROR]§r [Entity Texture Features]: " + obj), false);
            } else {
                ETF.LOGGER.error(obj);
            }
        } else {
            ETF.LOGGER.error(obj);
        }
    }

    public static boolean isExistingResource(Identifier identifier){
        if(identifier == null) return false;

        try{
            MinecraftClient.getInstance().getResourceManager().getResource(identifier);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static Optional<Resource> getResourceOptional(Identifier identifier){
        if(identifier == null) return Optional.empty();
        try{
            Resource notNull = MinecraftClient.getInstance().getResourceManager().getResource(identifier);
            if(notNull != null)
                return Optional.of(notNull);
        }catch (Exception ignored){}
        return Optional.empty();
    }

    public static NativeImage emptyNativeImage() {
        return emptyNativeImage(64, 64);
    }

    public static NativeImage emptyNativeImage(int Width, int Height) {
        NativeImage empty = new NativeImage(Width, Height, false);
        empty.fillRect(0, 0, Width, Height, 0);
        return empty;
    }

    public static boolean registerNativeImageToIdentifier(NativeImage image, Identifier identifier) {
        if (image == null || identifier == null) {
            logError("registering native image failed: " + image + ", " + identifier);
            return false;
        }
        try {
            NativeImage closableImage = new NativeImage(image.getWidth(), image.getHeight(), true);
            closableImage.copyFrom(image);

            MinecraftClient.getInstance().getTextureManager().destroyTexture(identifier);

            NativeImageBackedTexture closableBackedTexture = new NativeImageBackedTexture(closableImage);
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, closableBackedTexture);

            return true;
        } catch (Exception e) {
            logError("registering native image failed: " + e);
            return false;
        }

    }

    public static void checkModCompatibility() {
        for (ETFConfigWarning warning :
                ETFConfigWarnings.getRegisteredWarnings()) {
            warning.testWarningAndApplyFixIfEnabled();
        }
    }


    public interface RenderMethodForOverlay {
        void render(VertexConsumer consumer, int light);
    }

}
