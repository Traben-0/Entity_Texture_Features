package traben.entity_texture_features.client;


import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface ETF_1_18_2_versionPatch {

    static String getBiome(World world, BlockPos pos) {
        return world.getBiome(pos).getKey().toString().split("\s/\s")[1].replaceAll("[^0-9a-zA-Z_:-]", "");
    }
}
