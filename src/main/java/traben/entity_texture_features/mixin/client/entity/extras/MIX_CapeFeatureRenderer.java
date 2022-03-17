package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeFeatureRenderer.class)
public abstract class MIX_CapeFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>  {
    public MIX_CapeFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V", at = @At("HEAD"))
    private void injected(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        valueMain= null;
        if (abstractClientPlayerEntity.canRenderCapeTexture() && !abstractClientPlayerEntity.isInvisible() && abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE) && abstractClientPlayerEntity.getCapeTexture() != null) {
            ItemStack itemStack = abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST);
            if (!itemStack.isOf(Items.ELYTRA)) {
        valueMain = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(abstractClientPlayerEntity.getCapeTexture()));
            }
        }
    }

    @ModifyVariable(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/CapeFeatureRenderer;getContextModel()Lnet/minecraft/client/render/entity/model/EntityModel;", shift = At.Shift.BEFORE),
            index = 27)
    private VertexConsumer mixin(VertexConsumer value) {

        return  valueMain!= null ? valueMain : value;
    }

    private VertexConsumer valueMain= null;

}


