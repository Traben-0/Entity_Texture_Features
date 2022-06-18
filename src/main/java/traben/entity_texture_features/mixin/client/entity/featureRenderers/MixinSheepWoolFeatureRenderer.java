package traben.entity_texture_features.mixin.client.entity.featureRenderers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HorseMarkingFeatureRenderer;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.client.utils.ETFUtils;

import static traben.entity_texture_features.client.ETFClient.ETFConfigData;

@Mixin(SheepWoolFeatureRenderer.class)
public abstract class MixinSheepWoolFeatureRenderer extends FeatureRenderer<SheepEntity, SheepEntityModel<SheepEntity>>   {

    @Shadow
    @Final
    private static Identifier SKIN;


    public MixinSheepWoolFeatureRenderer(FeatureRendererContext<SheepEntity, SheepEntityModel<SheepEntity>> context) {
        super(context);
    }


    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/SheepEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/SheepWoolEntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                    shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)

    private void etf$applyEmissive(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SheepEntity sheepEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, MinecraftClient minecraftClient, boolean bl, VertexConsumer vertexConsumer) {
        //UUID id = livingEntity.getUuid();
        //todo no colour logic here for now, packs can set textures for this with the color properties anyway
        ETFUtils.generalEmissiveRenderModel(matrixStack, vertexConsumerProvider, etf$returnAlteredTexture(SKIN), (this.getContextModel()));
    }

    @Inject(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/SheepEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, SheepEntity sheepEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        etf$entity = sheepEntity;
    }

    SheepEntity etf$entity = null;

    @ModifyArg(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/passive/SheepEntity;FFFFFF)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/feature/SheepWoolFeatureRenderer;render(Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/client/render/entity/model/EntityModel;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFFFFF)V")
                    , index = 2)
    private Identifier etf$returnAlteredTexture(Identifier texture) {

        if (ETFConfigData.enableCustomTextures) {
            return ETFUtils.generalReturnAlteredFeatureTextureOrOriginal(texture, etf$entity);
        }
        return texture;
    }
}


