package traben.freshMobBehaviours.mixin.entity;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.freshMobBehaviours.Configurator2000;

import java.util.List;
import java.util.Objects;

@Mixin(Entity.class)
public abstract class MIX_Entity {


    @Inject(method = "isCollidable", at = @At("RETURN"), cancellable = true)
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        Entity self = (Entity)(Object)this;
        if (config.mobsCollideBetter && self instanceof LivingEntity){
            cir.setReturnValue(true);
        }
        cir.setReturnValue(cir.getReturnValue());
    }


  @Inject(method = "tick", at = @At("HEAD"))
  private void injected(CallbackInfo ci) {
      Entity self = (Entity)(Object)this;
      Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
      if (config.mobsCollideBetter && (self instanceof GhastEntity || self instanceof SlimeEntity)) {
          List<LivingEntity> bounce = self.world.getNonSpectatingEntities(LivingEntity.class, self.getBoundingBox().offset(0, 1, 0));
          bounce.remove(self);
          for (LivingEntity liv :
                  bounce) {
              if (liv.getY() > self.getY()) {
                  liv.setVelocity(liv.getVelocity().add(new Vec3d(0, liv.getVelocity().negate().getY() * 2, 0)));

              }
          }
      }
   }
}
