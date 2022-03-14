package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETF_METHODS;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(ElytraFeatureRenderer.class)
public abstract class MIX_ElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> implements ETF_METHODS {
    public MIX_ElytraFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Final
    @Shadow
    private ElytraEntityModel<T> elytra;
    @Final
    @Shadow
    private static
    Identifier SKIN;


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/ElytraEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER))
    private void applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        //UUID id = livingEntity.getUuid();
        if (ETFConfigData.enableElytra && ETFConfigData.enableEmissiveTextures) {
            Identifier identifier;
            if (livingEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
                if (abstractClientPlayerEntity.canRenderElytraTexture() && abstractClientPlayerEntity.getElytraTexture() != null) {
                    identifier = abstractClientPlayerEntity.getElytraTexture();
                } else if (abstractClientPlayerEntity.canRenderCapeTexture() && abstractClientPlayerEntity.getCapeTexture() != null && abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE)) {
                    identifier = abstractClientPlayerEntity.getCapeTexture();
                } else {
                    identifier = SKIN;
                }
            } else {
                identifier = SKIN;
            }

            String fileString = identifier.toString();
            if (Texture_Emissive.containsKey(fileString)) {
                if (Texture_Emissive.get(fileString) != null) {
                    VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(Texture_Emissive.get(fileString), true));
                    //one check most efficient instead of before and after applying
                    if (ETFConfigData.doShadersEmissiveFix) {
                        matrixStack.scale(1.01f, 1.01f, 1.01f);
                        elytra.render(matrixStack
                                , textureVert
                                , 15728640
                                , OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrixStack.scale(1f, 1f, 1f);
                    } else {
                        elytra.render(matrixStack
                                , textureVert
                                , 15728640
                                , OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                    }
                }
            } else {//creates and sets emissive for texture if it exists
                Identifier fileName_e;
                for (String suffix1 :
                        emissiveSuffix) {
                    fileName_e = new Identifier(fileString.replace(".png", suffix1 + ".png"));
                    if (isExistingFile(fileName_e)) {
                        VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getBeaconBeam(fileName_e, true));
                        Texture_Emissive.put(fileString, fileName_e);
                        //one check most efficient instead of before and after applying
                        if (ETFConfigData.doShadersEmissiveFix) {
                            matrixStack.scale(1.01f, 1.01f, 1.01f);
                            elytra.render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                            matrixStack.scale(1f, 1f, 1f);
                        } else {
                            elytra.render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
                        }
                        break;
                    }
                }
                if (!Texture_Emissive.containsKey(fileString)) {
                    Texture_Emissive.put(fileString, null);
                }
            }
        }
    }
}


