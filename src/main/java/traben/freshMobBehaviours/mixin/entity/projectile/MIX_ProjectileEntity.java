package traben.freshMobBehaviours.mixin.entity.projectile;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.FreshMobBehaviours;

@Mixin(ProjectileEntity.class)
public abstract class MIX_ProjectileEntity {
   @Inject(method = "onBlockHit", at = @At("HEAD"))
  private void injected(CallbackInfo ci) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (config.projectilesSetFire) {
            ProjectileEntity self = (ProjectileEntity) (Object) this;
            if (self.isOnFire() || self.wasOnFire) {
                //FreshMobBehaviours.setFire(self.getLandingPos(),self.world,0);
                FreshMobBehaviours.setFire(self.getBlockPos(), self.world, 0, true);
            }
        }
   }
}
