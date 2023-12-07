package traben.entity_texture_features.mixin.entity.renderer.feature;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Optional;

@Mixin(ShoulderParrotFeatureRenderer.class)
public abstract class MixinShoulderParrotFeatureRenderer<T extends PlayerEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {


    @Unique
    private NbtCompound entity_texture_features$parrotNBT = null;
    @Unique
    private ETFEntity entity_texture_features$player = null;

    @SuppressWarnings("unused")
    public MixinShoulderParrotFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context) {
        super(context);
    }

    @Inject(method = "method_17958(Lnet/minecraft/client/util/math/MatrixStack;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/client/render/VertexConsumerProvider;IFFFFLnet/minecraft/entity/EntityType;)V",
            at = @At(value = "HEAD"))
    private void etf$alterEntity(MatrixStack matrixStack, boolean bl, PlayerEntity playerEntity, NbtCompound nbtCompound, VertexConsumerProvider vertexConsumerProvider, int i, float f, float g, float h, float j, EntityType<?> type, CallbackInfo ci) {
        if (entity_texture_features$parrotNBT != null) {

            entity_texture_features$player = ETFRenderContext.getCurrentEntity();

            EntityType.getEntityFromNbt(entity_texture_features$parrotNBT, entity_texture_features$player.etf$getWorld());
            Optional<Entity> optionalEntity = EntityType.getEntityFromNbt(entity_texture_features$parrotNBT, entity_texture_features$player.etf$getWorld());
            if (optionalEntity.isPresent() && optionalEntity.get() instanceof ParrotEntity parrot) {
                ETFRenderContext.setCurrentEntity((ETFEntity) parrot);
            }
        }
    }

    @Inject(method = "method_17958(Lnet/minecraft/client/util/math/MatrixStack;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/client/render/VertexConsumerProvider;IFFFFLnet/minecraft/entity/EntityType;)V",
            at = @At(value = "RETURN"))
    private void etf$resetEntity(MatrixStack matrixStack, boolean bl, PlayerEntity playerEntity, NbtCompound nbtCompound, VertexConsumerProvider vertexConsumerProvider, int i, float f, float g, float h, float j, EntityType<?> type, CallbackInfo ci) {
        if (entity_texture_features$parrotNBT != null) {
            ETFRenderContext.setCurrentEntity(entity_texture_features$player);
        }
    }

    @Inject(method = "method_17958(Lnet/minecraft/client/util/math/MatrixStack;ZLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/nbt/NbtCompound;Lnet/minecraft/client/render/VertexConsumerProvider;IFFFFLnet/minecraft/entity/EntityType;)V",
            at = @At(value = "HEAD"))
    private <M extends Entity> void etf$getNBT(MatrixStack matrixStack, boolean bl, PlayerEntity playerEntity, NbtCompound nbtCompound, VertexConsumerProvider vertexConsumerProvider, int i, float f, float g, float h, float j, EntityType<M> type, CallbackInfo ci) {
        entity_texture_features$parrotNBT = nbtCompound;
    }
}


