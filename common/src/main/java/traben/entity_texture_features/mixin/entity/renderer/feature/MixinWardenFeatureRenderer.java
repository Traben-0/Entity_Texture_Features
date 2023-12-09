package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.WardenFeatureRenderer;
import net.minecraft.client.render.entity.model.WardenEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(WardenFeatureRenderer.class)
public abstract class MixinWardenFeatureRenderer<T extends WardenEntity, M extends WardenEntityModel<T>> extends FeatureRenderer<T, M> {

    @Unique
    private static final Identifier VANILLA_TEXTURE = new Identifier("textures/entity/warden/warden.png");
    @Shadow
    @Final
    private Identifier texture;

    @SuppressWarnings("unused")
    public MixinWardenFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Shadow
    protected abstract void unhideAllModelParts();

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/mob/WardenEntity;FFFFFF)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/WardenFeatureRenderer;updateModelPartVisibility()V",
                    shift = At.Shift.AFTER))
    private void etf$preventHiding(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T wardenEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (ETFConfigData.enableFullBodyWardenTextures && !VANILLA_TEXTURE.equals(texture)) {
            unhideAllModelParts();
        }
    }
}


