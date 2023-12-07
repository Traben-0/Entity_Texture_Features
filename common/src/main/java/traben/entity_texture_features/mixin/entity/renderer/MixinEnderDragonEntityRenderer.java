package traben.entity_texture_features.mixin.entity.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.utils.ETFUtils2;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(EnderDragonEntityRenderer.class)
public abstract class MixinEnderDragonEntityRenderer extends EntityRenderer<EnderDragonEntity> {

    @Final
    @Shadow
    private static Identifier TEXTURE;          // = new Identifier("textures/entity/enderdragon/dragon.png");
    @Final
    @Shadow
    private static Identifier EYE_TEXTURE;      // = new Identifier("textures/entity/enderdragon/dragon_eyes.png");
    @Final
    @Shadow
    private static RenderLayer DRAGON_CUTOUT;   //= RenderLayer.getEntityCutoutNoCull(TEXTURE);
    @Final
    @Shadow
    private static RenderLayer DRAGON_DECAL;    //= RenderLayer.getEntityDecal(TEXTURE);
    @Final
    @Shadow
    private static RenderLayer DRAGON_EYES;     //= RenderLayer.getEyes(EYE_TEXTURE);

    @SuppressWarnings("unused")
    protected MixinEnderDragonEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @ModifyArg(
            method = "render(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider;getBuffer(Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;"))
    private RenderLayer etf$returnAlteredTexture(RenderLayer texturedRenderLayer) {
        if (ETFConfigData.enableCustomTextures) {
            try {
                if (DRAGON_DECAL.equals(texturedRenderLayer)) {
                    return RenderLayer.getEntityDecal(TEXTURE);
                } else if (DRAGON_CUTOUT.equals(texturedRenderLayer)) {
                    return RenderLayer.getEntityCutoutNoCull(TEXTURE);
                } else if (DRAGON_EYES.equals(texturedRenderLayer)) {
                    return RenderLayer.getEyes(EYE_TEXTURE);
                }
            } catch (Exception e) {
                ETFUtils2.logError(e.toString(), false);
            }
        }
        return texturedRenderLayer;
    }

}


