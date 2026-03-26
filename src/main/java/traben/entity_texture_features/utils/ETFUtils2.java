package traben.entity_texture_features.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;

//#if MC >= 26.1
//$$ import net.minecraft.client.renderer.rendertype.RenderTypes;
//$$ import com.mojang.blaze3d.vertex.VertexMultiConsumer;
//$$ import net.minecraft.client.renderer.Sheets;
//#endif
//#if MC >= 12109
import net.minecraft.client.renderer.SubmitNodeCollector;
//#endif
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfigWarning;
import traben.entity_texture_features.config.ETFConfigWarnings;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.MutableComponent;

//#if MC >= 12103
import net.minecraft.util.ARGB;
//#endif

//#if MC >= 12004
import net.minecraft.network.chat.contents.PlainTextContents;
//#else
//$$ import net.minecraft.network.chat.contents.LiteralContents;
//#endif
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;


public abstract class ETFUtils2 {

    public static @NotNull ResourceLocation res(String fullPath){
        //#if MC >= 12100
        return ResourceLocation.parse(fullPath);
        //#else
        //$$ return new ResourceLocation(fullPath);
        //#endif
    }

    public static @NotNull ResourceLocation res(String namespace, String path){
        //#if MC >= 12100
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //#else
        //$$ return new ResourceLocation(namespace, path);
        //#endif
    }

    public static void setPixel(NativeImage image, int x, int y, int color) {
        //#if MC >= 12103
        image.setPixel(x, y, ARGB.toABGR(color));
        //#else
        //$$ image.setPixelRGBA(x, y, color);
        //#endif
    }

    public static int getPixel(NativeImage image, int x, int y) {
        //#if MC >= 12103
        return ARGB.fromABGR( image.getPixel(x, y));
        //#else
        //$$ return image.getPixelRGBA(x, y);
        //#endif
    }

    //#if MC >= 26.1
    //$$ public static final int FULL_BRIGHT = 15728880;
    //#else
    public static final int FULL_BRIGHT = net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;
    //#endif

    public static int packLight(int sky, int block) {
        //#if MC >= 26.1
        //$$ return sky << 4 | block << 20;
        //#else
        return net.minecraft.client.renderer.LightTexture.pack(sky, block);
        //#endif
    }

    //#if MC >= 12109
    public static void submitModelPart(final PoseStack matrixStack, final SubmitNodeCollector submit, final int light, final ModelPart modelPart, final ETFTexture etfTexture, @Nullable ETFEntityRenderState state) {
        var texture = etfTexture.getTextureIdentifier(state);
        var emissive = etfTexture.getEmissiveIdentifierOfCurrentState();
        var enchanted = etfTexture.getEnchantIdentifierOfCurrentState();
        submitModelPart(matrixStack, submit, light, modelPart, texture, emissive, enchanted);
    }

    public static void submitModelPart(final PoseStack matrixStack, final SubmitNodeCollector submit, final int light, final ModelPart modelPart, @NotNull final ResourceLocation texture, @Nullable final ResourceLocation emissive, @Nullable final ResourceLocation enchanted) {
        submit.submitModelPart(modelPart, matrixStack,
                //#if MC>= 12111
                //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                //#else
                RenderType
                //#endif
                        .entityTranslucent(texture), light, OverlayTexture.NO_OVERLAY, null);
        if (emissive != null) {
            submitEmissiveModelPart(matrixStack, submit, modelPart, emissive);
        }
    }

    public static void submitEmissiveModelPart(final PoseStack matrixStack, final SubmitNodeCollector submit, final ModelPart modelPart, final @NotNull ResourceLocation emissive) {
        submit.submitModelPart(modelPart, matrixStack,
                //#if MC>= 12111
                //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                //#else
                RenderType
                //#endif
                        .entityTranslucent(emissive), ETF.EMISSIVE_FEATURE_LIGHT_VALUE, OverlayTexture.NO_OVERLAY, null);
    }

    public static void submitEnchantedModelPart(final PoseStack matrixStack, final SubmitNodeCollector submit, final int light, final ModelPart modelPart, final @NotNull ResourceLocation enchanted) {
        submit.submitModelPart(modelPart, matrixStack,
                //#if MC>= 12111
                //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                //#else
                RenderType
                //#endif
                        .armorCutoutNoCull(enchanted), light, OverlayTexture.NO_OVERLAY, null,
                false, true);
    }
    //#endif

    public static int optifineHashing(int x) {
        // OptiFine integer hashing algorithm
        x ^= 0x3D ^ x >> 16;
        x += x << 3;
        x ^= x >> 4;
        x *= 668265261;
        x ^= x >> 15;
        return x;
    }

    @Deprecated // just to ide highlight usages
    public static void printDebugImage(NativeImage image) {
        if ((ETF.isFabric() == ETF.FABRIC_API) && ETF.getConfigDirectory() != null) {
            Path outputDirectory = Path.of(ETF.getConfigDirectory().toFile().getParent(), "\\ETF_debug_printout.png");
            try {
                image.writeToFile(outputDirectory);
                ETFUtils2.logMessage("printed debug image to: " + outputDirectory, false);
            } catch (Exception e) {
                ETFUtils2.logError(e.toString(), false);
            }
        }
    }

    public static ResourceLocation getETFVariantNotNullForInjector(ResourceLocation identifier) {
        // do not modify texture
        if (identifier == null
                || ETFRenderContext.getCurrentEntityState() == null
                || !ETFRenderContext.isAllowedToRenderLayerTextureModify())
            return identifier;

        // get etf modified texture
        ETFTexture etfTexture = ETFManager.getInstance().getETFTextureVariant(identifier, ETFRenderContext.getCurrentEntityState());
        if (ETFRenderContext.isAllowedToPatch()) {
            etfTexture.assertPatchedTextures();
        }
        ResourceLocation modified = etfTexture.getTextureIdentifier(ETFRenderContext.getCurrentEntityState());

        // check not null just to be safe, it shouldn't be however
        //noinspection ConstantValue
        return modified == null ? identifier : modified;
    }

    public static boolean renderEmissive(ETFTexture texture, MultiBufferSource provider, RenderMethodForOverlay renderer) {
        if (!ETF.config().getConfig().canDoEmissiveTextures()) return false;
        ResourceLocation emissive = texture.getEmissiveIdentifierOfCurrentState();
        if (emissive != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();

            VertexConsumer emissiveConsumer = provider.getBuffer(
                    ETFRenderContext.canRenderInBrightMode() ?

                            //#if MC>= 12111
                            //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                            //#else
                            RenderType
                            //#endif
                                    .beaconBeam(emissive, true) :
                            //#if MC < 12103
                            //$$     ETFRenderContext.shouldEmissiveUseCullingLayer() ?
                            //$$         RenderType.entityTranslucentCull(emissive) :
                            //#endif

                            //#if MC>= 12111
                            //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                            //#else
                            RenderType
                            //#endif
                                    .entityTranslucent(emissive));

            if (wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();

            ETFRenderContext.startSpecialRenderOverlayPhase();
            renderer.render(emissiveConsumer, ETF.EMISSIVE_FEATURE_LIGHT_VALUE);
            ETFRenderContext.endSpecialRenderOverlayPhase();
            return true;
        }
        return false;
    }

    //#if MC >= 26.1
    //$$ public static VertexConsumer getFoilBuffer(MultiBufferSource multiBufferSource, RenderType renderType, boolean bl, boolean bl2) {
    //$$     if (bl2) {
    //$$         return useTransparentGlint(renderType) ? VertexMultiConsumer.create(multiBufferSource.getBuffer(RenderTypes.glintTranslucent()), multiBufferSource.getBuffer(renderType)) : VertexMultiConsumer.create(multiBufferSource.getBuffer(bl ? RenderTypes.glint() : RenderTypes.entityGlint()), multiBufferSource.getBuffer(renderType));
    //$$     } else {
    //$$         return multiBufferSource.getBuffer(renderType);
    //$$     }
    //$$ }
    //$$
    //$$ private static boolean useTransparentGlint(RenderType renderType) {
    //$$     return Minecraft.useShaderTransparency() && (renderType == Sheets.translucentItemSheet() || renderType == Sheets.translucentBlockItemSheet());
    //$$ }
    //#endif

    public static boolean renderEnchanted(ETFTexture texture, MultiBufferSource provider, int light, RenderMethodForOverlay renderer) {
        // attempt enchanted render
        ResourceLocation enchanted = texture.getEnchantIdentifierOfCurrentState();
        if (enchanted != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();
            VertexConsumer enchantedVertex =
                    //#if MC >= 26.1
                    //$$ getFoilBuffer(provider, RenderTypes.armorCutoutNoCull(enchanted), false, true);
                    //#elseif MC>=12109
                    ItemRenderer.getFoilBuffer(provider,
                            //#if MC>= 12111
                            //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                            //#else
                            RenderType
                            //#endif
                                    .armorCutoutNoCull(enchanted), false, true);
                    //#else
                    //$$ ItemRenderer.getArmorFoilBuffer(provider,
                    //$$ RenderType.armorCutoutNoCull(enchanted),
                        //#if MC < 12100
                        //$$ false,
                        //#endif
                    //$$     true);
                    //#endif
            if (wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();

            ETFRenderContext.startSpecialRenderOverlayPhase();
            renderer.render(enchantedVertex, light);
            ETFRenderContext.endSpecialRenderOverlayPhase();
            return true;
        }
        return false;
    }

    @Nullable
    public static ResourceLocation addVariantNumberSuffix(@NotNull ResourceLocation identifier, int variant) {
        var changed = ETFUtils2.res(addVariantNumberSuffix(identifier.toString(), variant));
        return identifier.equals(changed) ? null : changed;
    }

    @NotNull
    public static String addVariantNumberSuffix(String identifierString, int variant) {
        if (variant < 2) return identifierString;

        String file = identifierString.endsWith(".png") ? "png" : identifierString.substring(identifierString.lastIndexOf('.') + 1);

        if (identifierString.matches("\\D+\\d+\\." + file)) {
            return identifierString.replace("." + file, "." + variant + "." + file);
        }
        return identifierString.replace("." + file, variant + "." + file);
    }

    @Nullable
    public static ResourceLocation replaceIdentifier(ResourceLocation id, String regex, String replace) {
        if (id == null) return null;
        try {
            return ETFUtils2.res(id.getNamespace(), id.getPath().replaceFirst(regex, replace));
        } catch (ResourceLocationException idFail) {
            ETFUtils2.logError(ETF.getTextFromTranslation("config.entity_texture_features.illegal_path_recommendation").getString() + "\n" + idFail);
        } catch (Exception ignored) {}
        return null;
    }

    @Nullable
    public static String returnNameOfHighestPackFromTheseMultiple(String[] packNameList) {
        ArrayList<String> packNames = new ArrayList<>(Arrays.asList(packNameList));
        // loop through and remove the one from the lowest pack of the first 2 entries
        // this iterates over the whole array
        final ArrayList<String> knownResourcepackOrder = ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER;
        while (packNames.size() > 1) {
            packNames.remove(knownResourcepackOrder.indexOf(packNames.get(0)) >= knownResourcepackOrder.indexOf(packNames.get(1)) ? 1 : 0);
        }
        // here the array is down to 1 entry which should be the one in the highest pack
        return packNames.get(0);
    }

    @Nullable
    public static String returnNameOfHighestPackFromTheseTwo(@Nullable String pack1, @Nullable String pack2) {
        if (pack1 == null) return null;
        if (pack1.equals(pack2) || pack2 == null) return pack1;

        return ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(pack1) >= ETFManager.getInstance().KNOWN_RESOURCEPACK_ORDER.indexOf(pack2) ? pack1 : pack2;
    }

    @Nullable
    public static Properties readAndReturnPropertiesElseNull(ResourceLocation path) {
        Properties props = new Properties();
        try (InputStream in = Minecraft.getInstance().getResourceManager().getResource(path).get().open()) {
            props.load(in);
            return props;
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public static List<Properties> readAndReturnAllLayeredPropertiesElseNull(ResourceLocation path) {
        List<Properties> props = new ArrayList<>();
        try {
            var resources = Minecraft.getInstance().getResourceManager().getResourceStack(path);
            for (Resource resource : resources) {
                if (resource == null) continue;
                try (InputStream in = resource.open()) {
                    Properties prop = new Properties();
                    prop.load(in);
                    if (!prop.isEmpty()) {
                        props.add(prop);
                    }
                } catch (Exception ignored) {}
            }
            return props.isEmpty() ? null : props;
        } catch (Exception e) {
            return null;
        }
    }

    public static NativeImage getNativeImageElseNull(@Nullable ResourceLocation identifier) {

        try {
            // try catch is intended
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(identifier);
            if (resource.isPresent()) {
                try (InputStream in = resource.get().open()) {
                    return NativeImage.read(in);
                } catch (Exception e) {
                    return null;
                }
            } else {
                AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(identifier);
                if (texture instanceof DynamicTexture nativeImageBackedTexture) {
                    var image2 = nativeImageBackedTexture.getPixels();
                    if (image2 == null) return null;
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

    // improvements to logging by @Maximum#8760
    public static void logMessage(String obj) {
        logMessage(obj, false);
    }

    public static void logMessage(String obj, boolean inChat) {
        if (inChat) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                //#if MC >= 26.1
                //$$ player.sendSystemMessage(MutableComponent.create(new PlainTextContents.LiteralContents(
                //$$     "§a[INFO]§r [ETF]: " + obj)));
                //#else
                player.displayClientMessage(MutableComponent.create(
                        //#if MC >= 12004
                        new PlainTextContents.LiteralContents
                        //#else
                        //$$ new LiteralContents
                        //#endif
                            ("§a[INFO]§r [ETF]: " + obj))/*.formatted(Formatting.GRAY, Formatting.ITALIC)*/ , false);
                //#endif
            } else {
                ETF.LOGGER.info("[ETF]: {}", obj);
            }
        } else {
            ETF.LOGGER.info("[ETF]: {}", obj);
        }
    }

    //improvements to logging by @Maximum#8760
    public static void logWarn(String obj) {
        logWarn(obj, false);
    }

    public static void logWarn(String obj, boolean inChat) {
        if (inChat) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                //#if MC >= 26.1
                //$$ player.sendSystemMessage(MutableComponent.create(new PlainTextContents.LiteralContents(
                //$$     "§e[WARN]§r [Entity Texture Features]: " + obj)).withStyle(ChatFormatting.YELLOW));
                //#else
                player.displayClientMessage(MutableComponent.create(
                        //#if MC >= 12004
                        new PlainTextContents.LiteralContents
                        //#else
                        //$$ new LiteralContents
                        //#endif
                                ("§e[WARN]§r [Entity Texture Features]: " + obj)).withStyle(ChatFormatting.YELLOW), false);
                //#endif
            } else {
                ETF.LOGGER.warn("[ETF]: {}", obj);
            }
        } else {
            ETF.LOGGER.warn("[ETF]: {}", obj);
        }
    }

    public static void logError(String obj) {
        logError(obj, false);
    }

    public static void logError(String obj, boolean inChat) {
        if (inChat) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                //#if MC >= 26.1
                //$$ player.sendSystemMessage(MutableComponent.create(new PlainTextContents.LiteralContents(
                //$$     "§4[ERROR]§r [Entity Texture Features]: " + obj)).withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                //#else
                player.displayClientMessage(MutableComponent.create(
                        //#if MC >= 12004
                        new PlainTextContents.LiteralContents
                        //#else
                        //$$ new LiteralContents
                        //#endif
                                ("§4[ERROR]§r [Entity Texture Features]: " + obj)).withStyle(ChatFormatting.RED, ChatFormatting.BOLD), false);
                //#endif
            } else {
                ETF.LOGGER.error("[ETF]: {}", obj);
            }
        } else {
            ETF.LOGGER.error("[ETF]: {}", obj);
        }
    }

    public static NativeImage emptyNativeImage() {
        return emptyNativeImage(64, 64);
    }

    public static NativeImage emptyNativeImage(int Width, int Height) {
        NativeImage empty = new NativeImage(Width, Height, false);
        empty.fillRect(0, 0, Width, Height, 0);
        return empty;
    }

    public static boolean registerNativeImageToIdentifier(NativeImage image, ResourceLocation identifier) {
        if (image == null || identifier == null) {
            logError("registering native image failed: " + image + ", " + identifier);
            return false;
        }
        try {
            Minecraft.getInstance().execute(() -> {
                try {
                    NativeImage closableImage = new NativeImage(image.getWidth(), image.getHeight(), true);
                    closableImage.copyFrom(image);

                    Minecraft.getInstance().getTextureManager().release(identifier);

                    DynamicTexture closableBackedTexture = new DynamicTexture(
                            //#if MC>=12105
                            null,
                            //#endif
                            closableImage);
                    Minecraft.getInstance().getTextureManager().register(identifier, closableBackedTexture);
                } catch (Exception e) {
                    logError("registering native image failed (inner): " + e);
                }
            });

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
