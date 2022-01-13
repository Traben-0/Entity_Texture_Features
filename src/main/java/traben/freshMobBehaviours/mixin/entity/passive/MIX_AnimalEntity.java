package traben.freshMobBehaviours.mixin.entity.passive;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import traben.freshMobBehaviours.Configurator2000;
import traben.freshMobBehaviours.FreshMobBehaviours;

import java.util.List;


@Mixin(AnimalEntity.class)
public abstract class MIX_AnimalEntity {

    /**
     * @author Traben
     * @reason better mob wandering behaviours
     */
    @Overwrite
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        AnimalEntity mob = ((AnimalEntity) (Object) this);
        Configurator2000 config = AutoConfig.getConfigHolder(Configurator2000.class).getConfig();
        int weight = 1;
        //basically vanilla below
        weight += world.getBlockState(pos.down()).isOf(Blocks.GRASS_BLOCK) ? 10 : 0;
        weight += world.getBlockState(pos.down()).isOf(Blocks.PODZOL) ? 10 : 0;

        if (config.animalsWanderBetter) {
            weight += world.getBlockState(pos.down()).isOf(Blocks.FARMLAND) ? 32 : 0;
            weight += world.getBlockState(pos.down()).isOf(Blocks.DIRT) ? 5 : 0;
            weight += world.getBlockState(pos).isOf(Blocks.GRASS) ? 15 : 0;
            //get out of rain
            if ((mob.world.isRaining() || mob.world.isNight()) && !world.isSkyVisible(pos)) {
                    weight += mob.world.isRaining() ? 512 : 25 ;
            }

            //breed items
            if (mob instanceof PigEntity) {
                if (FreshMobBehaviours.isBlockWithin2(pos, world, new Block[]{
                        Blocks.CARROTS,Blocks.POTATOES,Blocks.BEETROOTS
                })){
                    weight += 128;
                }
                if (FreshMobBehaviours.isBlockWithin2(pos, world, new Block[]{
                        Blocks.RED_MUSHROOM,Blocks.BROWN_MUSHROOM
                })){
                    weight += 32;
                }

            } else if ((mob instanceof SheepEntity
                    || mob instanceof CowEntity
                    || mob instanceof ChickenEntity
                    || mob instanceof HorseEntity )
                    &&(FreshMobBehaviours.isBlockWithin2(pos, world, new Block[]{Blocks.WHEAT}))){
                weight += 128;
            }
        }
        //light is big deal;
        weight *= (world.getLightLevel(pos));
        //get yo assess herding
        if (config.animalsHerd && (
                (mob instanceof CowEntity && config.doHerdCow)||
                        (mob instanceof PigEntity && config.doHerdPig)||
                        (mob instanceof ChickenEntity && config.doHerdChicken)||
                        (mob instanceof DonkeyEntity && config.doHerdDonkey)||
                        (mob instanceof HorseEntity && config.doHerdHorse)||
                        (mob instanceof SheepEntity && config.doHerdSheep)||
                        (mob instanceof GoatEntity && config.doHerdGoat)||
                        (mob instanceof LlamaEntity && config.doHerdLlama))) {
            List<AnimalEntity> herdlist = mob.world.getNonSpectatingEntities(AnimalEntity.class, new Box(pos).expand(6, 3, 6));

            if (!herdlist.isEmpty()) {
                herdlist.remove(mob);
                for (AnimalEntity a :
                        herdlist) {
                    if (a.getClass() == mob.getClass()) {
                        weight *= config.animalsHerdStrength;
                        break;
                    }
                }
            }
        }
        return weight;

    }
}
