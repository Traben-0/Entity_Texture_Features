package traben.entity_texture_features.mixin.entity.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WardenModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.WardenEmissiveLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.warden.Warden;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.utils.ETFUtils2;


@Mixin(WardenEmissiveLayer.class)
public abstract class MixinWardenFeatureRenderer<T extends Warden, M extends WardenModel<T>> extends RenderLayer<T, M> {

    @Unique
    private static final ResourceLocation VANILLA_TEXTURE = ETFUtils2.res("textures/entity/warden/warden.png");
    @Shadow
    @Final
    private ResourceLocation texture;

    @SuppressWarnings("unused")
    public MixinWardenFeatureRenderer(RenderLayerParent<T, M> context) {
        super(context);
    }


    @Shadow
    protected abstract void resetDrawForAllParts();

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/monster/warden/Warden;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/WardenEmissiveLayer;onlyDrawSelectedParts()V",
                    shift = At.Shift.AFTER))
    private void etf$preventHiding(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (ETF.config().getConfig().enableFullBodyWardenTextures && !VANILLA_TEXTURE.equals(texture)) {
            resetDrawForAllParts();
        }
    }
}


