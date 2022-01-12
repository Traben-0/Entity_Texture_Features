package traben.freshMobBehaviours.mixin.entity;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import traben.freshMobBehaviours.Configurator2000;

@Mixin(LivingEntity.class)
public abstract class MIX_LivingEntity {
    @Shadow
    public abstract void endCombat();

    @Shadow
    public abstract void enterCombat();

    /**
     * to buff sneak for good ol nighttime fear
     */
//reminder general mob distance view increased by *5
    @ModifyConstant(method = "getAttackDistanceScalingFactor", constant = @Constant(doubleValue = 0.8D))
    private double sneak(double value) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        double toReturn = config.stealthBuffSneak? 0.6D: 0.8D;
        if (config.stealthLeatherSneak) {
            LivingEntity entity = ((LivingEntity) (Object) this);

            for (ItemStack i :
                    entity.getArmorItems()) {
                if (i.isOf(Items.LEATHER_BOOTS)){
                    toReturn -= 0.2D;
                }else if(i.isOf(Items.LEATHER_LEGGINGS)
                        || i.isOf(Items.LEATHER_CHESTPLATE)
                        || i.isOf(Items.LEATHER_HELMET)) {
                    toReturn -= 0.04D;
                }
            }
            //return modifeied by targetting range
        }
        return    toReturn/(config.hostilesTargetRange>=2 ? config.hostilesTargetRange/2:1);
    }

    @ModifyConstant(method = "getAttackDistanceScalingFactor", constant = @Constant(doubleValue = 0.7D))
    private double invis(double value) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        double toReturn = config.stealthBuffInvisibility? 0.5D: 0.7D;
        if (config.stealthChainInvisibility) {
            LivingEntity entity = ((LivingEntity) (Object) this);
            for (ItemStack i :
                    entity.getArmorItems()) {
                if (i.isOf(Items.CHAINMAIL_BOOTS)
                        || i.isOf(Items.CHAINMAIL_LEGGINGS)
                        || i.isOf(Items.CHAINMAIL_CHESTPLATE)
                        || i.isOf(Items.CHAINMAIL_HELMET)) {
                    toReturn -= config.stealthBuffInvisibility? 0.1D : 0.15D;
                }
            }
        }
        return toReturn/(config.hostilesTargetRange>=2 ? config.hostilesTargetRange/2:1);
    }

    @ModifyConstant(method = "getAttackDistanceScalingFactor", constant = @Constant(doubleValue = 0.5D))
    private double heads(double value) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        if (config.stealthBuffHeads){
            return 0.0D;
        }else{
            return 0.5D;
        }
    }


    //   @Inject(method = "onDeath",  at = @At("HEAD"))
    //   private void creeperExplodedeath(CallbackInfo ci) {
    //     LivingEntity entt = ((LivingEntity)(Object)this);
    //    if (entt instanceof CreeperEntity) {
    //         CreeperEntity creep = ((CreeperEntity)(Object)this);
    //          creep.ex();
    //     }
    //  }
}//this.explode();



