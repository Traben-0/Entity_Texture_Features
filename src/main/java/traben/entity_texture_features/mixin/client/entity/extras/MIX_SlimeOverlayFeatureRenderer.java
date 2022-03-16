package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETF_METHODS;

import java.util.UUID;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(SlimeOverlayFeatureRenderer.class)
public abstract class MIX_SlimeOverlayFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, SlimeEntityModel<T>> implements ETF_METHODS {


    @Shadow @Final private EntityModel<T> model;

    public MIX_SlimeOverlayFeatureRenderer(FeatureRendererContext<T, SlimeEntityModel<T>> context) {
        super(context);
    }


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER))
    private void applyRenderFeatures(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
            if (ETFConfigData.enableEmissiveTextures) {
                String fileString = returnAlteredTexture(getTexture(livingEntity)).toString();
                if (Texture_Emissive.containsKey(fileString)) {
                    if (Texture_Emissive.get(fileString) != null) {
                        VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(Texture_Emissive.get(fileString), true));
                        //one check most efficient instead of before and after applying
                        if (ETFConfigData.doShadersEmissiveFix) {
                            matrixStack.scale(1.01f, 1.01f, 1.01f);
                            this.model.render(matrixStack
                                    , textureVert
                                    , 15728640
                                    , OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                            matrixStack.scale(1f, 1f, 1f);
                        } else {
                            this.model.render(matrixStack
                                    , textureVert
                                    , 15728640
                                    , OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                        }
                    }
                }
            }
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci){
        slime = livingEntity;
    }
    T slime = null;

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityTranslucent(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;",
                    ordinal = 0))
    private Identifier returnAlteredTexture(Identifier texture) {
        if (slime == null) return texture;
        UUID id = slime.getUuid();
            if (ETFConfigData.enableCustomTextures) {
                    if (UUID_randomTextureSuffix.containsKey(id)) {
                        if (UUID_randomTextureSuffix.get(id) != 0) {
                            return  returnOptifineOrVanillaIdentifier(texture.toString(), UUID_randomTextureSuffix.get(id));
                        }
                    }
            }
        return texture;
    }
}
