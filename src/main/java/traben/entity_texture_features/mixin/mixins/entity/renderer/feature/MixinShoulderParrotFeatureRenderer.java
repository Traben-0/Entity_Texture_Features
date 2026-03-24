package traben.entity_texture_features.mixin.mixins.entity.renderer.feature;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;

//#if MC >= 12103
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
//#endif
//#if MC >= 12106
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
//#endif
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.features.state.ETFEntityRenderState;
import traben.entity_texture_features.features.state.HoldsETFRenderState;
import traben.entity_texture_features.utils.ETFEntity;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

//#if MC >= 12109
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.EntitySpawnReason;

@Mixin(ParrotOnShoulderLayer.class)
public abstract class MixinShoulderParrotFeatureRenderer extends RenderLayer<AvatarRenderState, PlayerModel> {
//#elseif MC >= 12103
//$$ import net.minecraft.client.renderer.entity.state.PlayerRenderState;
//$$ import net.minecraft.world.entity.EntitySpawnReason;
//$$
//$$ @Mixin(ParrotOnShoulderLayer.class)
//$$ public abstract class MixinShoulderParrotFeatureRenderer extends RenderLayer<PlayerRenderState, PlayerModel> {
//#else
//$$ @Mixin(ParrotOnShoulderLayer.class)
//$$ public abstract class MixinShoulderParrotFeatureRenderer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
//#endif

//#if MC < 12109
//$$     @Unique
//$$     private ETFEntityRenderState etf$heldEntity = null;
//#endif

//#if MC >= 12109

    public MixinShoulderParrotFeatureRenderer() {super(null);}

    @Inject(method = "submitOnShoulder", at = @At(value = "INVOKE", target =
            //#if MC >= 26.1
            //$$ "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/Identifier;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
            //#else
            "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/RenderType;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
            //#endif
    ))
    private void etf$modifySubmit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int i, final AvatarRenderState avatarRenderState, final Parrot.Variant variant, final float f, final float g, final boolean bl, final CallbackInfo ci,
                                  @Local ParrotRenderState parrotRenderState) {
        var state =  ((HoldsETFRenderState) avatarRenderState).etf$getState();
        if (state != null && state.entity() instanceof Player playerEntity) {
            etf$setParrotAsCurrentEntity(playerEntity, parrotRenderState);
        }
    }

    @Unique
    private void etf$setParrotAsCurrentEntity(final Player playerEntity, final ParrotRenderState parrotRenderState) {
        if (parrotRenderState != null) {
            try {
                var parrot = EntityType.PARROT.create(playerEntity.level(), EntitySpawnReason.COMMAND);
//                if (optionalEntity instanceof Parrot parrot) {
                    //todo do i even need to set variant?
                    ETFRenderContext.setCurrentEntity(ETFEntityRenderState.forEntity((ETFEntity) parrot));
                    ((HoldsETFRenderState) parrotRenderState).etf$initState((ETFEntity) parrot);
//                }
            } catch (final Exception ignored) {}
        }
    }

    @Inject(method = "submitOnShoulder", at = @At(value = "RETURN"))
    private void etf$resetEntity(CallbackInfo ci, @Local AvatarRenderState avatarRenderState) {
        var state =  ((HoldsETFRenderState) avatarRenderState).etf$getState();
        if (ETFRenderContext.getCurrentEntityState() != state) {
            ETFRenderContext.setCurrentEntity(state);
        }
    }

//#elseif MC >= 12103
//$$
//$$     @Shadow @Final private ParrotRenderState parrotState;
//$$     public MixinShoulderParrotFeatureRenderer(final RenderLayerParent<PlayerRenderState, PlayerModel> renderLayerParent) {
//$$         super(renderLayerParent);
//$$     }
//$$
//$$     @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V",
//$$             at = @At(value = "HEAD"))
//$$     private void etf$alterEntityLeft(final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final PlayerRenderState playerRenderState, final float f, final float g, final CallbackInfo ci) {
//$$         etf$heldEntity = ETFRenderContext.getCurrentEntityState();
//$$         if (etf$heldEntity != null && etf$heldEntity.entity() instanceof Player playerEntity) {
//$$             etf$setParrotAsCurrentEntity(playerEntity, playerEntity.getShoulderEntityLeft());
//$$         }
//$$     }
//$$
//$$     @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V",
//$$             at = @At(value = "INVOKE",
//$$                     target = "Lnet/minecraft/client/renderer/entity/layers/ParrotOnShoulderLayer;renderOnShoulder(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;Lnet/minecraft/world/entity/animal/Parrot$Variant;FFZ)V"
//$$                 , shift = At.Shift.AFTER, ordinal = 0
//$$             )
//$$     )
//$$     private void etf$alterEntityRight(final PoseStack poseStack, final MultiBufferSource multiBufferSource, final int i, final PlayerRenderState playerRenderState, final float f, final float g, final CallbackInfo ci) {
//$$         etf$heldEntity = ETFRenderContext.getCurrentEntityState();
//$$         if (etf$heldEntity != null && etf$heldEntity.entity() instanceof Player playerEntity) {
//$$             etf$setParrotAsCurrentEntity(playerEntity, playerEntity.getShoulderEntityRight());
//$$         }
//$$     }
//$$
//$$     @Unique
//$$     private void etf$setParrotAsCurrentEntity(final Player playerEntity, final CompoundTag nbtCompound) {
//$$         if (nbtCompound != null) {
//$$             try {
//$$                 var optionalEntity = EntityType.PARROT.create(playerEntity.level(), EntitySpawnReason.COMMAND);
//$$                 if (optionalEntity instanceof Parrot parrot) {//null check
                    //#if MC>=12106
                    //$$ ValueInput valueInput = TagValueInput.create(ProblemReporter.DISCARDING,
                    //$$         HolderLookup.Provider.create(Stream.empty()), //todo what does this do?
                    //$$         nbtCompound);
                    //$$ optionalEntity.load(valueInput);
                    //#else
                    //$$ optionalEntity.load(nbtCompound);
                    //#endif
//$$                     ETFRenderContext.setCurrentEntity(ETFEntityRenderState.forEntity((ETFEntity) parrot));//todo state probably broke this
//$$                     ((HoldsETFRenderState)parrotState).etf$initState((ETFEntity) parrot);// todo does this work?
//$$                 }
//$$             } catch (final Exception ignored) {}
//$$         }
//$$     }
//$$
//$$     @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/PlayerRenderState;FF)V", at = @At(value = "RETURN"))
//$$     private void etf$resetEntity(CallbackInfo ci) {
//$$         if (etf$heldEntity != null) {
//$$             ETFRenderContext.setCurrentEntity(etf$heldEntity);
//$$         }
//$$         etf$heldEntity = null;
//$$     }
//$$
//#else
//$$ @SuppressWarnings("unused")
//$$ public MixinShoulderParrotFeatureRenderer(RenderLayerParent<T, PlayerModel<T>> context) {
//$$     super(context);
//$$ }
//$$
//$$     // cant target lambda directly with forge
//$$     @ModifyArg(method = "Lnet/minecraft/client/renderer/entity/layers/ParrotOnShoulderLayer;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/player/Player;FFFFZ)V",
//$$             at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
//$$     private Consumer<EntityType<?>> etf$alterEntity(final Consumer<EntityType<?>> action, @Local(argsOnly = true) T t, @Local CompoundTag nbtCompound) {
//$$         return (v)-> {
//$$             etf$HEADalterEntity(t, nbtCompound);
//$$             action.accept(v);
//$$             etf$TAILresetEntity();
//$$         };
//$$     }
//$$
//$$     @Unique
//$$     private void etf$HEADalterEntity(T playerEntity, CompoundTag nbtCompound) {
//$$         if (nbtCompound != null) {
//$$
//$$             etf$heldEntity = ETFRenderContext.getCurrentEntityState();
//$$
//$$             Optional<Entity> optionalEntity = EntityType.create(nbtCompound, playerEntity.level());
//$$             if (optionalEntity.isPresent() && optionalEntity.get() instanceof Parrot parrot) {
//$$                 ETFRenderContext.setCurrentEntity(ETFEntityRenderState.forEntity((ETFEntity) parrot));
//$$             }
//$$         }
//$$     }
//$$
//$$     @Unique
//$$     private void etf$TAILresetEntity() {
//$$         if (etf$heldEntity != null) {
//$$             ETFRenderContext.setCurrentEntity(etf$heldEntity);
//$$         }
//$$         etf$heldEntity = null;
//$$     }
//$$
//#endif
}


