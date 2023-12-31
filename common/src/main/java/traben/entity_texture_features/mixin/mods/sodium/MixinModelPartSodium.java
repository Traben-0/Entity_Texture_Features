package traben.entity_texture_features.mixin.mods.sodium;

import me.jellysquid.mods.sodium.client.render.immediate.model.EntityRenderer;
import me.jellysquid.mods.sodium.client.render.vertex.VertexConsumerUtils;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.texture_handlers.ETFTexture;
import traben.entity_texture_features.mixin.MixinModelPart;
import traben.entity_texture_features.utils.ETFVertexConsumer;

/**
 * this is a copy of {@link MixinModelPart} but for sodium's alternative model part render method
 * <p>
 * this should have no negative impact on sodium's render process, other than of course adding more code that needs to run
 */
@Pseudo
@Mixin(value = EntityRenderer.class)
public abstract class MixinModelPartSodium {

    @Shadow
    public static void render(MatrixStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color) {
    }

    @Inject(method = "render",
            at = @At(value = "HEAD"))
    private static void etf$findOutIfInitialModelPart(MatrixStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color, CallbackInfo ci) {
        ETFRenderContext.incrementCurrentModelPartDepth();
    }

    @Inject(method = "render",
            at = @At(value = "RETURN"))
    private static void etf$doEmissiveIfInitialPart(MatrixStack matrixStack, VertexBufferWriter writer, ModelPart part, int light, int overlay, int color, CallbackInfo ci) {
        //run code if this is the initial topmost rendered part
        if (ETFRenderContext.getCurrentModelPartDepth() != 1) {
            ETFRenderContext.decrementCurrentModelPartDepth();
        } else {
            if (ETFRenderContext.isCurrentlyRenderingEntity()
                    && writer instanceof ETFVertexConsumer etfVertexConsumer) {
                ETFTexture texture = etfVertexConsumer.etf$getETFTexture();
                if(texture != null && (texture.isEmissive() || texture.isEnchanted())) {
                    VertexConsumerProvider provider = etfVertexConsumer.etf$getProvider();
                    RenderLayer layer = etfVertexConsumer.etf$getRenderLayer();
                    if (provider != null && layer != null) {
                        //attempt special renders as eager OR checks
                        if (etf$renderEmissive(texture, provider, matrixStack, part, overlay, color) |
                                etf$renderEnchanted(texture, provider, matrixStack, part, light, overlay, color)) {
                            //reset render layer stuff behind the scenes if special renders occurred
                            provider.getBuffer(layer);
                        }
                    }
                }
            }
            //ensure model count is reset
            ETFRenderContext.resetCurrentModelPartDepth();
        }
    }

    @Unique
    private static boolean etf$renderEmissive(ETFTexture texture, VertexConsumerProvider provider, MatrixStack matrices, ModelPart part, int overlay, int color) {
        Identifier emissive = texture.getEmissiveIdentifierOfCurrentState();
        if (emissive != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();

            boolean textureIsAllowedBrightRender = ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT
                    && ETFRenderContext.getCurrentEntity().etf$canBeBright();// && !ETFRenderContext.getCurrentETFTexture().isPatched_CurrentlyOnlyArmor();

            VertexConsumer emissiveConsumer = provider.getBuffer(
                    textureIsAllowedBrightRender ?
                            RenderLayer.getBeaconBeam(emissive, true) :
                            ETFRenderContext.getCurrentEntity().etf$isBlockEntity() ?
                                    RenderLayer.getEntityTranslucentCull(emissive) :
                                    RenderLayer.getEntityTranslucent(emissive));

            if(wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();

            //sodium
            VertexBufferWriter writer = VertexConsumerUtils.convertOrLog(emissiveConsumer);

            if (writer == null) {
                return false;
            }
            //

            ETFRenderContext.startSpecialRenderOverlayPhase();
                render(matrices, writer, part, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, color);
            ETFRenderContext.endSpecialRenderOverlayPhase();
            return true;
        }
        return false;
    }

    @Unique
    private static boolean etf$renderEnchanted(ETFTexture texture, VertexConsumerProvider provider, MatrixStack matrices, ModelPart part, int light, int overlay, int color) {
        //attempt enchanted render
        Identifier enchanted = texture.getEnchantIdentifierOfCurrentState();
        if (enchanted != null) {
            boolean wasAllowed = ETFRenderContext.isAllowedToRenderLayerTextureModify();
            ETFRenderContext.preventRenderLayerTextureModify();
                VertexConsumer enchantedVertex = ItemRenderer.getArmorGlintConsumer(provider, RenderLayer.getArmorCutoutNoCull(enchanted), false, true);
            if(wasAllowed) ETFRenderContext.allowRenderLayerTextureModify();

            //sodium
            VertexBufferWriter writer = VertexConsumerUtils.convertOrLog(enchantedVertex);

            if (writer == null) {
                return false;
            }
            //

            ETFRenderContext.startSpecialRenderOverlayPhase();
                render(matrices, writer, part, light, overlay, color);
            ETFRenderContext.endSpecialRenderOverlayPhase();
            return true;
        }
        return false;
    }


}
