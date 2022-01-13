package traben.freshMobBehaviours.mixin.entity.hostile;

import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GhastEntity.class)
public class MIX_GhastEntity {
    @Inject(method = "initGoals", at = @At("HEAD"))
    private void adjustSpeedByDistance(CallbackInfo ci) {
        //test
            //((GhastEntity)(Object)this).verticalCollision = true;

    }
}
