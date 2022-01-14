package traben.freshMobBehaviours.mixin.entity.projectile;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.Blocks;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.FreshMobBehaviours;

import java.util.Random;

@Mixin(ProjectileEntity.class)
public abstract class MIX_ProjectileEntity {
   @Inject(method = "onBlockHit", at = @At("HEAD"))
  private void injected(CallbackInfo ci) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (config.projectilesSetFire) {
            ProjectileEntity self = (ProjectileEntity) (Object) this;
            if (self.isOnFire() || self.wasOnFire) {
                FreshMobBehaviours.setFire(self.getBlockPos(), self.world, 0, true);

            }
        }
   }
    @Inject(method = "tick", at = @At("HEAD"))
    private void random(CallbackInfo ci) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (config.projectilesSetFire) {
            ProjectileEntity self = (ProjectileEntity) (Object) this;
            Random rand = new Random();
            if (self.isOnFire() || self.wasOnFire) {
                FreshMobBehaviours.setFire(self.getBlockPos(), self.world, rand.nextInt(80), false);

            }
        }
    }
}
