package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import traben.entity_texture_features.client.ETFUtils;
import traben.entity_texture_features.client.redirect.TextureRedirectManager;

@Mixin(ArmorFeatureRenderer.class)
public abstract class MixinArmorFeatureRenderer<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public MixinArmorFeatureRenderer(FeatureRendererContext<T, M> context, Identifier getArmorTexture) {
        super(context);
    }

    @Shadow
    protected abstract Identifier getArmorTexture(ArmorItem item, boolean legs, @Nullable String overlay);

    @Inject(method = "renderArmorParts", at = @At(value = "TAIL", shift = At.Shift.BEFORE))
    private void etf$applyEmissive(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean usesSecondLayer, A model, boolean legs, float red, float green, float blue, String overlay, CallbackInfo ci) {
        ETFUtils.renderEmissiveModel(matrices, vertexConsumers, getArmorTexture(item, legs, overlay), model, true);
    }

    @Inject(method = "getArmorTexture", at = @At("RETURN"), cancellable = true)
    private void etf$redirectTexture(ArmorItem armorItem, boolean legs, String overlay, CallbackInfoReturnable<Identifier> cir) {
        cir.setReturnValue(TextureRedirectManager.redirect(cir.getReturnValue(), null));
    }
}
