package traben.entity_texture_features.mixin.entity.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETFClientCommon;

@Mixin(ArmorStandEntityRenderer.class)
public abstract class MixinArmorStandEntityRenderer extends LivingEntityRenderer<ArmorStandEntity, ArmorStandArmorEntityModel> {


    public MixinArmorStandEntityRenderer(EntityRendererFactory.Context ctx, ArmorStandArmorEntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "getRenderLayer(Lnet/minecraft/entity/decoration/ArmorStandEntity;ZZZ)Lnet/minecraft/client/render/RenderLayer;", at = @At("RETURN"),
            cancellable = true)
    private void etf$fixCallToSuperForETF(ArmorStandEntity armorStandEntity, boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<RenderLayer> cir) {
        // etf requires a call to the super method
        // etf doesn't care if it's a marker
        if (ETFClientCommon.ETFConfigData.enableCustomTextures || ETFClientCommon.ETFConfigData.enableEmissiveTextures)
            cir.setReturnValue(super.getRenderLayer(armorStandEntity, bl, bl2, bl3));
    }

}


