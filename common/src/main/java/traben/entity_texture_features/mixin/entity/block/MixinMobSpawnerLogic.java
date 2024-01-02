package traben.entity_texture_features.mixin.entity.block;

import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(MobSpawnerLogic.class)
public abstract class MixinMobSpawnerLogic {


    @Unique
    private static final long ETF$SPAWNER_MARKER = (12345L << 32) + 12345L;

    @Inject(method = "getRenderedEntity",
            at = @At(value = "RETURN"))
    private void etf$stabiliseMobSpawnerUUID(World world, BlockPos pos, CallbackInfoReturnable<Entity> cir) {
        Entity entity = cir.getReturnValue();
        if(entity != null){
            entity.setUuid(new UUID(pos.asLong(), ETF$SPAWNER_MARKER));
            entity.setPos(pos.getX(),pos.getY(),pos.getZ());
            //resulting nbt should be [I;?,?,12345,12345]
        }
    }

//copy of how nbt splits up uuid values
//    private static int[] toIntArray(long uuidMost, long uuidLeast) {
//        return new int[]{(int)(uuidMost >> 32), (int)uuidMost, (int)(uuidLeast >> 32), (int)uuidLeast};
//    }
}


