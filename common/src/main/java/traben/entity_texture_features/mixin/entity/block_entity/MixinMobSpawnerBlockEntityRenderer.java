package traben.entity_texture_features.mixin.entity.block_entity;

import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static traben.entity_texture_features.texture_features.ETFManager.ETF_GENERIC_UUID;

@Mixin(MobSpawnerBlockEntityRenderer.class)
public abstract class MixinMobSpawnerBlockEntityRenderer implements BlockEntityRenderer<BellBlockEntity> {


    @ModifyArg(method = "render(Lnet/minecraft/block/entity/MobSpawnerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
            ), index = 0)
    private Entity etf$addUUID(Entity entity) {
        entity.setUuid(ETF_GENERIC_UUID);
        return entity;

    }
}


