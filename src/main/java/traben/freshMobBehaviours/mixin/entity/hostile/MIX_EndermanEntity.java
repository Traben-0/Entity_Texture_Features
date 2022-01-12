package traben.freshMobBehaviours.mixin.entity.hostile;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(EndermanEntity.class)
public abstract class MIX_EndermanEntity {
    @Shadow abstract boolean teleportTo(Entity entity);

    @Inject(method = "mobTick", at = @At("HEAD"))
    private void adjustSpeedByDistance(CallbackInfo ci) {
        try{
        EndermanEntity self = (EndermanEntity) (Object)this;
            PlayerEntity player = Objects.requireNonNull(self.world.getClosestPlayer(self, -1));
        if (self.canSee(player) && !player.getAbilities().creativeMode){
            self.lookAtEntity(player,150,70);
        }else if(!player.getAbilities().creativeMode
                && !self.world.isClient
                && self.world.getServer().getOverworld() == self.getWorld()
                && self.getRandom().nextInt(300)==1
        ){
            teleportTo(player);
        }
        }catch(Exception e){
//nahfam
        }
    }
}
