package traben.freshMobBehaviours.mixin.entity.hostile;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import traben.freshMobBehaviours.Configurator2000;

@Mixin(DrownedEntity.class)
public abstract class MIX_DrownedEntity {
    @ModifyArg(
            method = "initCustomGoals",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/DrownedEntity$DrownedAttackGoal;<init>(Lnet/minecraft/entity/mob/DrownedEntity;DZ)V"),
            index = 1
    )
    private double attackgoal(double value) {
        return adjustSpeed(value);
    }
    @ModifyArg(
            method = "initCustomGoals",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/DrownedEntity$LeaveWaterGoal;<init>(Lnet/minecraft/entity/mob/DrownedEntity;D)V"),
            index = 1
    )
    private double leavewatergoal(double value) {
        return adjustSpeed(value);
    }
    @ModifyArg(
            method = "initCustomGoals",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/DrownedEntity$TargetAboveWaterGoal;<init>(Lnet/minecraft/entity/mob/DrownedEntity;DI)V"),
            index = 1
    )
    private double targetabovewatergoal(double value) {
        return adjustSpeed(value);
    }

    private static double adjustSpeed(double value) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (config.drownedSwimFaster) {
            return 1.5;
        } else {
            return value;
        }
    }
}