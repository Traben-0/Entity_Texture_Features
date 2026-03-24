package traben.entity_texture_features.mixin.mixins.entity.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.utils.ETFUtils2;






@Mixin(EnderDragonRenderer.class)
public abstract class MixinEnderDragonEntityRenderer extends
//#if MC >= 12103
EntityRenderer<EnderDragon, net.minecraft.client.renderer.entity.state.EnderDragonRenderState>
//#else
//$$    EntityRenderer<EnderDragon>
//#endif
{

    //#if MC < 26.1
    @Final
    @Shadow
    private static ResourceLocation DRAGON_LOCATION;          // = new Identifier("textures/entity/enderdragon/dragon.png");
    @Final
    @Shadow
    private static RenderType RENDER_TYPE;   //= RenderLayer.getEntityCutoutNoCull(TEXTURE);
    @Final
    @Shadow
    private static RenderType DECAL;    //= RenderLayer.getEntityDecal(TEXTURE);
    //#endif

    @Final
    @Shadow
    private static ResourceLocation DRAGON_EYES_LOCATION;      // = new Identifier("textures/entity/enderdragon/dragon_eyes.png");

    @Final
    @Shadow
    private static RenderType EYES;     //= RenderLayer.getEyes(EYE_TEXTURE);

    @SuppressWarnings("unused")
    protected MixinEnderDragonEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    //#if MC >= 12109
    //#if MC < 26.1
    @ModifyArg(method = "submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/OrderedSubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
    private RenderType etf$returnAlteredTexture(RenderType texturedRenderLayer) { return getType(texturedRenderLayer); }
    //#endif

    @ModifyArg(method = "submit(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
    private RenderType etf$returnAlteredTexture2(RenderType texturedRenderLayer) { return getType(texturedRenderLayer); }
    //#else
    //$$ @ModifyArg(method =
        //#if MC >= 12103
        //$$ "render(Lnet/minecraft/client/renderer/entity/state/EnderDragonRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        //#else
        //$$ "render(Lnet/minecraft/world/entity/boss/enderdragon/EnderDragon;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
        //#endif
    //$$     at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    //$$ private RenderType etf$returnAlteredTexture(RenderType texturedRenderLayer) {
    //$$     return getType(texturedRenderLayer);
    //$$ }
    //#endif

    @Unique
    private static @Nullable RenderType getType(final RenderType texturedRenderLayer) {
        if (ETF.config().getConfig().canDoCustomTextures()) {
            try {
                //#if MC < 26.1
                // recreate each frame so ETF can modify
                if (DECAL.equals(texturedRenderLayer)) {
                    return
                            //#if MC>= 12111
                            //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                            //#else
                            RenderType
                            //#endif
                                    .entityDecal(DRAGON_LOCATION);
                } else if (RENDER_TYPE.equals(texturedRenderLayer)) {
                    return
                            //#if MC>= 12111
                            //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                            //#else
                            RenderType
                            //#endif
                                    .entityCutoutNoCull(DRAGON_LOCATION);
                } else
                //#endif
                if (EYES.equals(texturedRenderLayer)) {
                    return
                            //#if MC>= 12111
                            //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                            //#else
                            RenderType
                            //#endif
                                    .eyes(DRAGON_EYES_LOCATION);
                }
            } catch (Exception e) {
                ETFUtils2.logError(e.toString(), false);
            }
        }
        return texturedRenderLayer;
    }

}


