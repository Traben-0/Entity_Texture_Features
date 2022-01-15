package traben.freshMobBehaviours.mixin.entity;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.FreshMethods;
import traben.freshMobBehaviours.FreshMobBehaviours;

import java.util.Random;
import java.util.Set;

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
    private int fireCount=0;
    private int freezecount=0;
    @Inject(method = "tick", at = @At("TAIL"))
    private void healAllMobsRandomly(CallbackInfo ci) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        LivingEntity self = ((LivingEntity) (Object) this);
        if (config.mobsHeal
                &&self.getHealth() < self.getMaxHealth()
                && !self.isUndead()
                && !(self instanceof PlayerEntity)
                && !(self instanceof IronGolemEntity)
                && !(self instanceof RavagerEntity)
                && !(self instanceof WitherEntity)//probably is undead lol cbf checking
                && !(self instanceof EnderDragonEntity)
                //&& !(self instanceof ShulkerEntity)
               //&& !(self instanceof VexEntity) )
                && self.getRandom().nextInt(500) == 0){
            self.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200));
        }
        if((self.isOnFire() || self.wasOnFire)) {
            fireCount++;
            if (fireCount > config.mobsFlameFrequencySeconds * 20
                    && config.mobsBurnSpreadFireIfPlayerClose
                    && self.world.getNonSpectatingEntities(PlayerEntity.class, self.getBoundingBox().expand(config.mobsFireRangeFromPlayer)).size() > 0) {
                FreshMethods.setFire(self.getBlockPos(), self.world, config.mobsFlameChance);
                fireCount = 0;
            }
        }
        if (self.isFreezing()) {
            freezecount++;
            if (self.world.isClient()
                    && freezecount > 20) {
                self.world.addParticle(ParticleTypes.SNOWFLAKE, self.getX(), self.getY() + 1, self.getZ(), MathHelper.nextBetween(self.getRandom(), -1.0F, 1.0F) * 0.083333336F, 0.05000000074505806D, MathHelper.nextBetween(self.getRandom(), -1.0F, 1.0F) * 0.083333336F);
                self.world.addParticle(ParticleTypes.SNOWFLAKE, self.getX(), self.getY() + 2, self.getZ(), (double) (MathHelper.nextBetween(self.getRandom(), -1.0F, 1.0F)) * 0.083333336F, 0.05000000074505806D, MathHelper.nextBetween(self.getRandom(), -1.0F, 1.0F) * 0.083333336F);
                self.world.addParticle(ParticleTypes.SNOWFLAKE, self.getX(), self.getY() + 1.5, self.getZ(), MathHelper.nextBetween(self.getRandom(), -1.0F, 1.0F) * 0.083333336F, 0.05000000074505806D, MathHelper.nextBetween(self.getRandom(), -1.0F, 1.0F) * 0.083333336F);
                freezecount = 0;
            }
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void deathFireSpread(CallbackInfo ci) {
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        LivingEntity self = ((LivingEntity) (Object) this);
        if (config.mobsBurnSpreadFireIfPlayerClose
                && (self.isOnFire() || self.wasOnFire)
                //dont burn drops for player kills

                && self.world.getNonSpectatingEntities(PlayerEntity.class, self.getBoundingBox().expand(35)).size() >0){
            if (!(self.getAttacker() instanceof PlayerEntity)) {
                FreshMethods.setFire(self.getBlockPos(), self.world, config.mobsFlameChance);
                FreshMethods.setFire(self.getBlockPos().north(), self.world, config.mobsFlameChance);
                FreshMethods.setFire(self.getBlockPos().east(), self.world, config.mobsFlameChance);
                FreshMethods.setFire(self.getBlockPos().west(), self.world, config.mobsFlameChance);
                FreshMethods.setFire(self.getBlockPos().south(), self.world, config.mobsFlameChance);
            }else if (self.world.isClient){
                self.world.playSound(self.world.getClosestPlayer(self,-1),self.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
        }

        if (!self.world.isClient && self.hasCustomName()) {
            //  LOGGER.info("Named entity {} died: {}", this, this.getDamageTracker().getDeathMessage().getString())
            FreshMethods.sendGlobalMessage(self,"Named Mob: "+self.getDamageTracker().getDeathMessage().getString());
        }
    }



}



