package traben.entity_texture_features.client;
/*
* file is always overridden by working 1.18 version of class
*  -> ETF_1_18_1_versionPatch.class
 */

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("ALL")
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface ETF_1_18_1_versionPatch {

    static String getBiome(World world, BlockPos pos){
        return"";//  Objects.requireNonNull(world.getRegistryManager().get(Registry.BIOME_KEY).getId(world.getBiome(pos))).toString();
    }
}
