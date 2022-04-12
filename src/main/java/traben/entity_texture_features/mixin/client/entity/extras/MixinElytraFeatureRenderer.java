package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.client.ETFUtils;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;

@Mixin(ElytraFeatureRenderer.class)
public abstract class MixinElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    public MixinElytraFeatureRenderer(FeatureRendererContext<T, M> context) {
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
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void etf$applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, ItemStack itemStack, Identifier identifier, VertexConsumer vertexConsumer) {
        //UUID id = livingEntity.getUuid();

//before i understood local capture
//            Identifier identifier;
//            if (livingEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
//                if (abstractClientPlayerEntity.canRenderElytraTexture() && abstractClientPlayerEntity.getElytraTexture() != null) {
//                    identifier = abstractClientPlayerEntity.getElytraTexture();
//
//                } else if (abstractClientPlayerEntity.canRenderCapeTexture() && abstractClientPlayerEntity.getCapeTexture() != null && abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE)) {
//                    identifier = abstractClientPlayerEntity.getCapeTexture();
//
//                } else {
//                    identifier = SKIN;
//                }
//            } else {
//                identifier = SKIN;
//            }
        if (ETFConfigData.enableElytra && ETFConfigData.enableEmissiveTextures) {
            ETFUtils.generalEmissiveRenderModel(matrixStack, vertexConsumerProvider, identifier, elytra);
        }
    }
}


