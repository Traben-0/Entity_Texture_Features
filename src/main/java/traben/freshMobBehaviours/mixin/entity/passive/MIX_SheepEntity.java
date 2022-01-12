package traben.freshMobBehaviours.mixin.entity.passive;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.SheepEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.FreshMobBehaviours;

@Mixin(SheepEntity.class)
public class MIX_SheepEntity {
    @Inject(method = "initGoals", at = @At("HEAD"))
    private void goalAdjustments(CallbackInfo ci) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (config.animalsGetSpooked && config.doSpookSheep){
            FreshMobBehaviours.animalSpookedGoalAdjustments(((PathAwareEntity) (Object) this));
        }
    }
}
