package traben.entity_texture_features.mixin.mixins.entity.renderer;

//#if MC < 26.1
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EndCrystalRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.ETF;

@Mixin(EndCrystalRenderer.class)
public abstract class MixinEndCrystalRenderer {

    @Shadow @Final private static ResourceLocation END_CRYSTAL_LOCATION;

    //#if MC >= 12109
    @ModifyArg(method = "submit(Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", ordinal = 0))
    //#elseif MC >= 12103
    //$$ @ModifyArg(method = "render(Lnet/minecraft/client/renderer/entity/state/EndCrystalRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    //$$ at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    //#else
    //$$ @ModifyArg(method = "render(Lnet/minecraft/world/entity/boss/enderdragon/EndCrystal;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    //$$         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    //#endif
    private RenderType etf$modifyTexture(final RenderType renderType) {
        if (ETF.config().getConfig().canDoCustomTextures()) {
            // recreate each frame so ETF can modify
            return
                    //#if MC>= 12111
                    //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                    //#else
                    RenderType
                    //#endif
                            .entityCutoutNoCull(END_CRYSTAL_LOCATION);
        }
        return renderType;
    }
}
//#else
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import traben.entity_texture_features.mixin.CancelTarget;
//$$
//$$ @Mixin(CancelTarget.class)
//$$ public class MixinEndCrystalRenderer {}
//#endif

