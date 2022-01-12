package traben.freshMobBehaviours.mixin.entity;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.freshMobBehaviours.Configurator2000;

import java.util.List;
import java.util.Objects;


@Mixin(WolfEntity.class)
public abstract class MIX_WolfEntity {

    int wolfCount = 0;
    private int waiter = 150;

    @Inject(method = "tick", at = @At("TAIL"))
    private void makeEvil(CallbackInfo ci) {
        if (!((WolfEntity) (Object) this).world.isClient) {
            if (this.waiter != 0) this.waiter--;
            WolfEntity wolf = ((WolfEntity) (Object) this);
            Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
            if ((config.wolfCanRandomlyBreed || config.wolfIsHostile)
                   && this.waiter == 0 && !wolf.isBaby() && !wolf.isTamed() && wolf.getAngerTime() <= 0 && wolf.getLoveTicks() <= 0) {
                try {
                    PlayerEntity playerTarg = Objects.requireNonNull(wolf.world.getClosestPlayer(wolf, -1));
                    //get angrey if see player
                    if (config.wolfIsHostile && wolf.canSee(playerTarg) && !playerTarg.getAbilities().creativeMode) {
                        wolf.setAngryAt(playerTarg.getUuid());
                        wolf.setAngerTime(300);
                    } else if (config.wolfCanRandomlyBreed){
                        //try breed if cant see player
                        int wolfCounttemp = 0;
                        List<WolfEntity> list = wolf.world.getNonSpectatingEntities(WolfEntity.class, wolf.getBoundingBox().expand(256.0D, 256.0D, 256.0D));
                        for (WolfEntity a :
                                list) {
                            wolfCounttemp++;
                        }
                        if (wolfCounttemp != wolfCount) {
                            wolfCount = wolfCounttemp;
                            System.out.println("wolfCount " + wolfCount);
                        }
                        int chance = wolfCount;
                        if (chance <= 1) {
                            chance = 2;
                        }
                        if (wolf.getRandom().nextInt(chance) == 1 && wolfCount <= 50 && wolf.world.isDay()) {
                            if (wolfCount > 2) {
                                //if can see another wolf set both to breed
                                for (WolfEntity other : list) {
                                    if (wolf != other && wolf.canSee(other) && canBreedWith2(other, true, wolf)) {
                                         wolf.setLoveTicks(300);
                                        wolf.world.sendEntityStatus(wolf, (byte) 18);
                                        wolf.setPersistent();
                                        other.setLoveTicks(300);
                                        other.world.sendEntityStatus(wolf, (byte) 18);
                                        other.setPersistent();
                                        break;
                                    }
                                }
                            }//random chance to just spawn baby etc if they are too spread out AND IF NOT ANGRY OR LOVE
                            if (wolf.getRandom().nextInt(100) == 1) {
                                ServerWorld servWrld = Objects.requireNonNull(wolf.getServer()).getOverworld();
                                WolfEntity wolfy = wolf.createChild(wolf.getServer().getOverworld(), wolf);
                                if (wolfy != null) {
                                    wolfy.setBaby(true);
                                    wolfy.refreshPositionAndAngles(wolf.getX(), wolf.getY(), wolf.getZ(), 0.0F, 0.0F);
                                    servWrld.spawnEntityAndPassengers(wolfy);
                                    servWrld.sendEntityStatus(wolf, (byte) 18);
                                }
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    System.out.println("wolf AI exception - skipping task");
                }
                this.waiter = 150;
            }
        }

    }

    /**
     * @author Traben
     * @reason because method redirection literally has never worked
     */
    @Overwrite
    public boolean canBreedWith(AnimalEntity other) {
        return canBreedWith2(other, false, ((WolfEntity) (Object) this));
    }

    public boolean canBreedWith2(AnimalEntity other, Boolean ignoreCanBreed, WolfEntity wolf) {
        if (other == wolf) {
            return false;
            // } else if (!this.isTamed()) {  ignoring
            //  return false;
        } else if (!(other instanceof WolfEntity wolfEntity)) {
            return false;
        } else {
            // if (!wolfEntity.isTamed()) {  ignoring
            //    return false;
            // } else
            if (wolfEntity.isInSittingPose()) {
                return false;
            } else {
                return (ignoreCanBreed || wolf.isInLove()) && (ignoreCanBreed || wolfEntity.isInLove())
                        //will breed if both wild or both tamed
                        && (wolf.isTamed() == wolfEntity.isTamed());
            }
        }
    }

}
//this.convertTo(EntityType.DROWNED)
//WOLF = register("wolf", EntityType.Builder.create(WolfEntity::new, SpawnGroup.MONSTER).setDimensions(0.6F, 0.85F).maxTrackingRange(10));