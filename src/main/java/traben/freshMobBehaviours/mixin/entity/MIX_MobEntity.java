package traben.freshMobBehaviours.mixin.entity;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MIX_MobEntity {

    @Inject(method = "detachLeash", at = @At("HEAD"))
    private void injected(CallbackInfo ci) {
        MobEntity self = (MobEntity) (Object)this;
   if (self.world.isClient){
       self.world.playSound(self.world.getClosestPlayer(self,-1),self.getBlockPos(),SoundEvents.ENTITY_LEASH_KNOT_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);
   }
    }
}
