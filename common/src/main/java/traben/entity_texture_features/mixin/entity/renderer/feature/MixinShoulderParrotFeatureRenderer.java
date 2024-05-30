package traben.entity_texture_features.mixin.entity.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Optional;

@Mixin(ParrotOnShoulderLayer.class)
public abstract class MixinShoulderParrotFeatureRenderer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {



    @Unique
    private ETFEntity etf$heldEntity = null;

    @SuppressWarnings("unused")
    public MixinShoulderParrotFeatureRenderer(RenderLayerParent<T, PlayerModel<T>> context) {
        super(context);
    }

    @Inject(method = "method_17958",
            at = @At(value = "HEAD"))
    private void etf$alterEntity(PoseStack matrixStack, boolean bl, Player playerEntity, CompoundTag nbtCompound, MultiBufferSource vertexConsumerProvider, int i, float f, float g, float h, float j, EntityType<?> type, CallbackInfo ci) {
        if (nbtCompound != null) {

            etf$heldEntity = ETFRenderContext.getCurrentEntity();

            EntityType.create(nbtCompound, playerEntity.level());
            Optional<Entity> optionalEntity = EntityType.create(nbtCompound, playerEntity.level());
            if (optionalEntity.isPresent() && optionalEntity.get() instanceof Parrot parrot) {
                ETFRenderContext.setCurrentEntity((ETFEntity) parrot);
            }
        }
    }

    @Inject(method = "method_17958",
            at = @At(value = "RETURN"))
    private void etf$resetEntity(PoseStack matrixStack, boolean bl, Player playerEntity, CompoundTag nbtCompound, MultiBufferSource vertexConsumerProvider, int i, float f, float g, float h, float j, EntityType<?> type, CallbackInfo ci) {
        if (etf$heldEntity != null) {
            ETFRenderContext.setCurrentEntity(etf$heldEntity);
        }
        etf$heldEntity = null;
    }


}


