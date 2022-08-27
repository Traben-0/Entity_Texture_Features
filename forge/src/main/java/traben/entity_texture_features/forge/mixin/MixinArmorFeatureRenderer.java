package traben.entity_texture_features.forge.mixin;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFTexture;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    private ETFTexture thisETFTexture = null;

    public MixinArmorFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @ModifyArg(method = "renderModel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private Identifier etf$changeTexture(Identifier texture) {
        thisETFTexture = ETFManager.getETFTexture(texture, null, ETFManager.TextureSource.ENTITY_FEATURE);
        return thisETFTexture.getTextureIdentifier(null, ETFConfigData.enableEmissiveTextures);
    }

    @Inject(method = "renderModel",
            at = @At(value = "TAIL"))
    private void etf$applyEmissive(MatrixStack arg, VertexConsumerProvider arg2, int i, boolean bl, Model arg3, float f, float g, float h, Identifier armorResource, CallbackInfo ci) {
        //UUID id = livingEntity.getUuid();
        if (thisETFTexture != null && ETFConfigData.enableEmissiveTextures) {
            Identifier emissive = thisETFTexture.getEmissiveIdentifierOfCurrentState();
            if (emissive != null) {
                VertexConsumer textureVert;// = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(PATH_EMISSIVE_TEXTURE_IDENTIFIER.get(fileString), true), false, usesSecondLayer);
                //if (ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT) {
                //    textureVert = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getBeaconBeam(emissive, true), false, usesSecondLayer);
                //} else {
                textureVert = arg2.getBuffer(RenderLayer.getArmorCutoutNoCull(emissive));
                //}
                arg3.render(arg, textureVert, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, f, g, h, 1.0F);
            }
        }

    }

}


