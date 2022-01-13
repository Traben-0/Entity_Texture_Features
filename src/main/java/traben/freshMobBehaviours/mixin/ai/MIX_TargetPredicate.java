package traben.freshMobBehaviours.mixin.ai;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.freshMobBehaviours.Configurator2000;

@Mixin(TargetPredicate.class)
public class MIX_TargetPredicate {

    private LivingEntity self;
    private  LivingEntity target;

    @Inject(method = "test", at = @At("HEAD"))
    private void alwaysSeePlayerWhenClose(LivingEntity baseEntity, LivingEntity targetEntity, CallbackInfoReturnable<Boolean> cir) {
        self = baseEntity;
        target = targetEntity;
        if (baseEntity instanceof HostileEntity) {
            Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
            if (config.hostileCanSenseClosePlayer
                    && targetEntity instanceof PlayerEntity
                    && Math.sqrt(baseEntity.squaredDistanceTo(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ())) <= 12) {
                ((TargetPredicate) (Object) this).ignoreVisibility();
            }
        }
    }


//@ModifyVariable(method = "setBaseMaxDistance", at = @At("HEAD"), ordinal = 0, argsOnly = true)
  //  private double largerTargetRange(double x) {

     //   return   * x;
   // }
     //
     @ModifyArg(method = "test", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(DD)D"), index = 0)
     private double injected(double x) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (self instanceof HostileEntity) {
            return config.hostilesTargetRange * x;
        } else
            if(self instanceof AnimalEntity){
                if (target instanceof PlayerEntity && target.isSneaking()){
                    //undo sneak modifier set by hostiles range
                    return x*(config.hostilesTargetRange>=2 ? config.hostilesTargetRange/2:1);
                }
        }
        return x;
    }
}
