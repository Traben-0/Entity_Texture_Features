package traben.freshMobBehaviours.mixin.entity.hostile;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.FreshMobBehaviours;

import java.util.Objects;

@Mixin(CreeperEntity.class)
public abstract class MIX_CreeperEntity {


    //EntityAttributeModifier modifier ;//=new EntityAttributeModifier("BOOST",0.4D, EntityAttributeModifier.Operation.MULTIPLY_BASE);
    //private int count = 0;
    @Shadow
    private void explode() {}

    @Inject(method = "tick",  at = @At("HEAD"))
        private void makeExplodeDeath(CallbackInfo ci) {

        if (!((CreeperEntity) (Object) this).world.isClient) {
            CreeperEntity creep = ((CreeperEntity) (Object) this);
            Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
            if (config.creeperExplodeOnDeath && creep.getHealth()<=0) {
                if (!creep.world.isClient) {
                    this.explode();
                    if (creep.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                        try {
                            Identifier identifier = creep.getLootTable();
                            LootTable lootTable = creep.world.getServer().getLootManager().getTable(identifier);
                            net.minecraft.loot.context.LootContext.Builder builder = getLootContextBuilder2(creep.getRecentDamageSource().getAttacker() instanceof PlayerEntity, creep.getRecentDamageSource());
                            lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), creep::dropStack);
                        }catch(Exception e){
                            System.out.println("no gunpowder 4 u");
                        }
                    }
                }
            }///speed modifiers
//            this.count++;
//            if (this.count >= 20) {
//                PlayerEntity closest = creep.getWorld().getClosestPlayer(creep, -1);
//                if (closest != null) {
//                    if (modifier != null) {
//                        Objects.requireNonNull(creep.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).removeModifier(modifier);
//                    }
//                    modifier = new EntityAttributeModifier("BOOST",
//                            FreshMobBehaviours.slowDownToVanillaByTarget(creep, closest, config.creeperBaseSpeedModifier, config.creeperDashSpeedModifier,config.hostilesCanDash&&config.creeperCanDash),
//                            EntityAttributeModifier.Operation.MULTIPLY_BASE);
//                    Objects.requireNonNull(creep.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).addTemporaryModifier(modifier);
//                }
//            }
        }
    }
    protected net.minecraft.loot.context.LootContext.Builder getLootContextBuilder2(boolean causedByPlayer, DamageSource source) {
        CreeperEntity creep = ((CreeperEntity) (Object) this);
        net.minecraft.loot.context.LootContext.Builder builder = (new net.minecraft.loot.context.LootContext.Builder((ServerWorld)creep.world)).random(creep.getRandom()).parameter(LootContextParameters.THIS_ENTITY, creep).parameter(LootContextParameters.ORIGIN, creep.getPos()).parameter(LootContextParameters.DAMAGE_SOURCE, source).optionalParameter(LootContextParameters.KILLER_ENTITY, source.getAttacker()).optionalParameter(LootContextParameters.DIRECT_KILLER_ENTITY, source.getSource());
        if (causedByPlayer && creep.getAttacker() != null) {
            PlayerEntity player = (PlayerEntity) creep.getAttacker();
            builder = builder.parameter(LootContextParameters.LAST_DAMAGE_PLAYER, player).luck(player.getLuck());
        }

        return builder;
    }

}//this.explode();
