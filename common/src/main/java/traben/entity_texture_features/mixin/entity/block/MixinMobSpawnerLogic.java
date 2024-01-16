package traben.entity_texture_features.mixin.entity.block;


import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.ETFApi;

import java.util.UUID;

@Mixin(MobSpawnerBlockEntityRenderer.class)
public abstract class MixinMobSpawnerLogic {


    @ModifyArg(
            method = "render(Lnet/minecraft/block/entity/MobSpawnerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"),
            index = 0
    )
    private Entity etf$stabiliseMobSpawnerUUID(Entity entity) {
        if(entity != null){
            entity.setUuid(new UUID(entity.getBlockPos().asLong(), ETFApi.ETF_SPAWNER_MARKER));

            //resulting nbt should be [I;?,?,12345,12345]
        }
        return entity;
    }

//copy of how nbt splits up uuid values
//    private static int[] toIntArray(long uuidMost, long uuidLeast) {
//        return new int[]{(int)(uuidMost >> 32), (int)uuidMost, (int)(uuidLeast >> 32), (int)uuidLeast};
//    }
}


