package traben.entity_texture_features.mixin.mixins;


import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.state.ETFState;

@Mixin(value = LevelRenderer.class, priority = 1010) // Compat to let sodium 1.21.2-8 mixin apply firsy
public class MixinLevelRenderer {
    //#if MC >= 1.21.9
    @Inject(method = { "submitEntities" , "submitBlockEntities" }, at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
    //#elseif MC >= 1.21.2
    //$$ @Inject(method = { "renderEntities" , "renderBlockEntities" }, at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"), require = 1) // See MixinSodiumWorldRenderer for require
    //#else
    //$$ @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"), slice =
    //$$     @org.spongepowered.asm.mixin.injection.Slice(from = @At(value = "CONSTANT", args = "stringValue=entities"), to = @At(value = "CONSTANT", args = "stringValue=destroyProgress")))
    //#endif
    private void emf$verify(final CallbackInfo ci) {
        // These are the top level, stack should be clean
        ETFState.stackVerifyEmpty();
    }

    //#if MC >= 1.21.9
    @Inject(method = { "submitEntities" , "submitBlockEntities" }, at = @At(value = "TAIL"))
    //#elseif MC >= 1.21.2
    //$$ @Inject(method = { "renderEntities" , "renderBlockEntities" }, at = @At(value = "TAIL"), require = 1) // See MixinSodiumWorldRenderer for require
    //#else
    //$$ @Inject(method = "renderLevel", at = @At(value = "CONSTANT", args = "stringValue=destroyProgress"))
    //#endif
    private void emf$verifyEnd(final CallbackInfo ci) {
        // These are the top level, stack should be clean
        ETFState.stackVerifyEmpty();
    }
}