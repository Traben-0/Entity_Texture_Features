package traben.entity_texture_features.mixin.mixins.mods.sodium;

//#if !SODIUM || MC < 1.21.2 || MC >= 1.21.9
import org.spongepowered.asm.mixin.Mixin;
import traben.entity_texture_features.mixin.CancelTarget;

@Mixin(CancelTarget.class)
public class MixinSodiumWorldRenderer { }
//#else
//$$ import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Pseudo;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$ import traben.entity_texture_features.features.state.ETFState;
//$$
//$$ // Sodium uses a mixin override on these version to entirely replace the block entity rendering
//$$ // It's already no longer an issue in modern versions
//$$
//$$ @Pseudo
//$$ @Mixin(SodiumWorldRenderer.class)
//$$ public class MixinSodiumWorldRenderer { // See MixinLevelRenderer
//$$     @Inject(method = "renderBlockEntity(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderBuffers;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDDLnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/client/player/LocalPlayer;Lcom/llamalad7/mixinextras/sugar/ref/LocalBooleanRef;)V",
//$$             at = @At(value = "HEAD"), remap = false)
//$$     private static void emf$verify(final CallbackInfo ci) {
//$$         // These are the top level, stack should be clean
//$$         ETFState.stackVerifyEmpty();
//$$     }
//$$
//$$     @Inject(method = "renderBlockEntity(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderBuffers;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;FLnet/minecraft/client/renderer/MultiBufferSource$BufferSource;DDDLnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/client/player/LocalPlayer;Lcom/llamalad7/mixinextras/sugar/ref/LocalBooleanRef;)V",
//$$             at = @At(value = "TAIL"), remap = false)
//$$     private static void emf$verifyEnd(final CallbackInfo ci) {
//$$         // These are the top level, stack should be clean
//$$         ETFState.stackVerifyEmpty();
//$$     }
//$$ }
//#endif