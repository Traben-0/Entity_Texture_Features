package traben.freshMobBehaviours.mixin.entity.hostile;

import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class MIX_ZombieEntity {
  //  @Inject(method = "tick", at = @At("HEAD"))
  //  private void injected(CallbackInfo ci) {
   //
   // }
}
