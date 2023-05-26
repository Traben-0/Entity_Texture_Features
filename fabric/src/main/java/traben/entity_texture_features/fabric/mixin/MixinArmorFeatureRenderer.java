package traben.entity_texture_features.fabric.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.config.screens.ETFConfigScreen;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFTexture;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    private ETFTexture thisETFTexture = null;

    @SuppressWarnings("unused")
    public MixinArmorFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @ModifyArg(method = "renderArmorParts",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"),index = 0)
    private Identifier etf$changeTexture(Identifier texture) {
        thisETFTexture = ETFManager.getInstance().getETFTexture(texture, null, ETFManager.TextureSource.ENTITY_FEATURE, ETFConfigData.removePixelsUnderEmissiveArmour);
        //noinspection ConstantConditions
        if (thisETFTexture != null) {
            return thisETFTexture.getTextureIdentifier(null, ETFConfigData.enableEmissiveTextures);
        }
        return texture;
    }

    @Inject(method = "renderArmorParts",
            at = @At(value = "TAIL"))
    private void etf$applyEmissive(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, A model, boolean secondTextureLayer, float red, float green, float blue, String overlay, CallbackInfo ci) {
        //UUID id = livingEntity.getUuid();
        if (thisETFTexture != null && ETFConfigData.enableEmissiveTextures) {
            Identifier emissive = thisETFTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert;// = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
                //if (ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT) {
                //    textureVert = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(emissive, true));//ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(emissive, true), false, usesSecondLayer);
                //} else {
                textureVert = vertexConsumers.getBuffer(RenderLayer.getArmorCutoutNoCull(emissive)); //ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getEntityTranslucent(emissive), false, usesSecondLayer);
                //}
                model.render(matrices, textureVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0F);
            }
        }

    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"), cancellable = true)
    private void etf$cancelIfUi(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (MinecraftClient.getInstance() != null) {
            if (MinecraftClient.getInstance().currentScreen instanceof ETFConfigScreen) {
                //cancel armour rendering
                ci.cancel();
            }
        }
    }

}


