package traben.freshMobBehaviours.mixin.entity.hostile;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.FreshMobBehaviours;

import java.util.Objects;

@Mixin(SpiderEntity.class)
public class MIX_SpiderEntity {


//    EntityAttributeModifier modifier ;//=new EntityAttributeModifier("BOOST",0.4D, EntityAttributeModifier.Operation.MULTIPLY_BASE);
//    private int count = 0;
//
//    //make mobs faster further from players
//    @Inject(method = "tick", at = @At("HEAD"))
//    private void adjustSpeedByDistance(CallbackInfo ci) {
//
//        if (!((SpiderEntity) (Object) this).world.isClient) {
//            this.count++;
//            if (this.count >= 20) {
//                SpiderEntity spidey = ((SpiderEntity) (Object) this);
//                PlayerEntity closest = spidey.getWorld().getClosestPlayer(spidey, -1);
//                if (closest != null) {
//                    if (modifier != null) {
//                        Objects.requireNonNull(spidey.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).removeModifier(modifier);
//                    }
//                    Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
//                    modifier = new EntityAttributeModifier("BOOST",
//                            FreshMobBehaviours.slowDownToVanillaByTarget(spidey, closest, config.spiderBaseSpeedModifier, config.spiderDashSpeedModifier,config.hostilesCanDash&&config.spiderCanDash),
//                            EntityAttributeModifier.Operation.MULTIPLY_BASE);
//                    Objects.requireNonNull(spidey.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).addTemporaryModifier(modifier);
//                }
//            }
//        }
//    }
}
