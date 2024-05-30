package traben.entity_texture_features.mixin.entity.block;

import net.minecraft.core.BlockPos;
#if MC <= MC_20_2
import net.minecraft.util.RandomSource;
#endif
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.ETFApi;

import java.util.UUID;

@Mixin(BaseSpawner.class)
public abstract class MixinMobSpawnerLogic {


    @Inject(method = "getOrCreateDisplayEntity",
            at = @At(value = "RETURN"))
    private void etf$stabiliseMobSpawnerUUID(Level world, #if MC <= MC_20_2 RandomSource randomSource, #endif BlockPos pos, CallbackInfoReturnable<Entity> cir) {
        Entity entity = cir.getReturnValue();
        if (entity != null) {
            entity.setUUID(new UUID(pos.asLong(), ETFApi.ETF_SPAWNER_MARKER));

            //resulting nbt should be [I;?,?,12345,12345]
        }
    }

//copy of how nbt splits up uuid values
//    private static int[] toIntArray(long uuidMost, long uuidLeast) {
//        return new int[]{(int)(uuidMost >> 32), (int)uuidMost, (int)(uuidLeast >> 32), (int)uuidLeast};
//    }
}


