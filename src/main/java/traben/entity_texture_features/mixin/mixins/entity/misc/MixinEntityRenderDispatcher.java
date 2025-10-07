package traben.entity_texture_features.mixin.mixins.entity.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.utils.ETFEntity;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {
    //#if MC >= 12103
        //#if MC>= 12109
        @Inject(method = "submit", at = @At(value = "HEAD"))
        //#elseif MC>= 12105
        //$$ @Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;DDDLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
        //$$         at = @At(value = "HEAD"))
        //#else
        //$$ @Inject(method = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
        //$$         at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"))
        //#endif
    private <S extends net.minecraft.client.renderer.entity.state.EntityRenderState>
        void etf$grabContext(final CallbackInfo ci, @SuppressWarnings("LocalMayBeArgsOnly") @Local S state) {
        ETFRenderContext.setCurrentEntity(((HoldsETFRenderState) state).etf$getState());
    }
    //#else
    //$$ @Inject(method = "render",
    //$$     at = @At(value = "HEAD"))
    //$$ private <E extends net.minecraft.world.entity.Entity> void etf$grabContext(CallbackInfo ci, @Local(argsOnly = true) E entity) {
    //$$     ETFRenderContext.setCurrentEntity(ETFEntityRenderState.forEntity((ETFEntity) entity));
    //$$ }
    //#endif

    @Inject(method =
            //#if MC>= 12109
            "submit",
            //#elseif MC >= 12103
            //$$ "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            //#else
            //$$     "render",
            //#endif
            at = @At(value = "RETURN"))
    private void etf$clearContext(CallbackInfo ci) {
        ETFRenderContext.reset();
    }

}
