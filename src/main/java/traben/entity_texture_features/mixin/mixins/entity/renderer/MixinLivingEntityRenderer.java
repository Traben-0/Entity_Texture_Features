package traben.entity_texture_features.mixin.mixins.entity.renderer;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.utils.ETFEntity;

//#if MC >= 12103
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends EntityRenderer<T, S> {
//#else
//$$ import net.minecraft.client.renderer.entity.RenderLayerParent;
//$$ @Mixin(LivingEntityRenderer.class)
//$$ public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements RenderLayerParent<T, M> {
//#endif


    @SuppressWarnings("unused")
    protected MixinLivingEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);

    }

    //#if MC >= 12109
    private static final String RENDER = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V";
    //#elseif MC >= 12103
    //$$ private static final String RENDER = "render(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";
    //#else
    //$$ private static final String RENDER = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V";
    //#endif

    @Inject(method = RENDER, at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private void etf$markFeatures(CallbackInfo ci, @Share("shareState") LocalRef<ETFEntityRenderState> etf$heldEntity
            //#if MC>= 12103
               , @Local(argsOnly = true) net.minecraft.client.renderer.entity.state.LivingEntityRenderState state
            ) { etf$heldEntity.set(((HoldsETFRenderState) state).etf$getState());
            //#else
            //$$     , @Local(argsOnly = true) net.minecraft.world.entity.LivingEntity entity
            //$$ ) { etf$heldEntity.set(ETFEntityRenderState.forEntity((ETFEntity) entity));
            //#endif
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.setRenderingFeatures(true);
    }

    @Inject(method = RENDER, at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    private void etf$markFeaturesLoopEnd(CallbackInfo ci, @Share("shareState") LocalRef<ETFEntityRenderState> etf$heldEntity) {
        // assert main entity each loop in case of other entities within feature renderer
        ETFRenderContext.setCurrentEntity(etf$heldEntity.get());
        ETFRenderContext.allowRenderLayerTextureModify();
        ETFRenderContext.endSpecialRenderOverlayPhase();
    }

    @Inject(method = RENDER, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"))
    private void etf$markFeaturesEnd(CallbackInfo ci) {
        ETFRenderContext.setRenderingFeatures(false);
    }


}


