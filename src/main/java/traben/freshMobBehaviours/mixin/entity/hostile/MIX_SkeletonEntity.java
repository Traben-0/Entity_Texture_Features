package traben.freshMobBehaviours.mixin.entity.hostile;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.FreshMobBehaviours;

import java.util.Objects;

@Mixin(SkeletonEntity.class)
public class MIX_SkeletonEntity {

//    EntityAttributeModifier modifier;//=new EntityAttributeModifier("BOOST",0.4D, EntityAttributeModifier.Operation.MULTIPLY_BASE);
//    private int count = 0;
//
//    //make mobs faster further from players
//    @Inject(method = "tick", at = @At("HEAD"))
//    private void adjustSpeedByDistance(CallbackInfo ci) {
//
//        if (!((AbstractSkeletonEntity) (Object) this).world.isClient) {
//            this.count++;
//            if (this.count >= 20) {
//                SkeletonEntity skelly = ((SkeletonEntity) (Object) this);
//                PlayerEntity closest = skelly.getWorld().getClosestPlayer(skelly, -1);
//                //speed modify
//                if (closest != null) {
//                    if (modifier != null) {
//                        Objects.requireNonNull(skelly.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).removeModifier(modifier);
//                    }
//                    Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
//                    modifier = new EntityAttributeModifier("BOOST",
//                            FreshMobBehaviours.slowDownToVanillaByTarget(skelly, closest, config.skeletonBaseSpeedModifier, config.skeletonDashSpeedModifier,config.hostilesCanDash&&config.skeletonCanDash),
//                            EntityAttributeModifier.Operation.MULTIPLY_BASE);
//                    Objects.requireNonNull(skelly.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).addTemporaryModifier(modifier);
//                }
//                //put back bow pull if view blocked
//                //BowItem theBow = (BowItem)skelly.getArrowType(skelly.getStackInHand(ProjectileUtil.getHandPossiblyHolding(skelly, Items.BOW))).getItem();
//
//
//
//            }
//        }
//    }
}
