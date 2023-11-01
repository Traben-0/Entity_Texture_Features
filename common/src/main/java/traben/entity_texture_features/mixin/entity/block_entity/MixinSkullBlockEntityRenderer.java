package traben.entity_texture_features.mixin.entity.block_entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.texture_features.ETFManager;
import traben.entity_texture_features.texture_features.texture_handlers.ETFPlayerTexture;
import traben.entity_texture_features.texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.entity_wrappers.ETFBlockEntityWrapper;
import traben.entity_texture_features.utils.entity_wrappers.ETFEntity;
import traben.entity_texture_features.utils.entity_wrappers.ETFPlayerEntity;
import traben.entity_texture_features.utils.entity_wrappers.ETFPlayerHeadWrapper;

import java.util.Map;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(SkullBlockEntityRenderer.class)
public abstract class MixinSkullBlockEntityRenderer implements BlockEntityRenderer<BedBlockEntity> {


    @SuppressWarnings("StaticCollection")
    @Shadow
    @Final
    private static Map<SkullBlock.SkullType, Identifier> TEXTURES;
    @Unique
    private ETFTexture entity_texture_features$thisETFTexture = null;
    @Unique
    private ETFPlayerTexture entity_texture_features$thisETFPlayerTexture = null;
    @Unique
    private ETFEntity etf$entity = null;

    @Unique
    private static Identifier etf$getIdentifier(SkullBlock.SkullType type, @Nullable GameProfile profile) {
        Identifier identifier = TEXTURES.get(type);
        if (type == SkullBlock.Type.PLAYER && profile != null) {
            PlayerSkinProvider playerSkinProvider = MinecraftClient.getInstance().getSkinProvider();
            return playerSkinProvider.getSkinTextures(profile).texture();
        } else {
            return identifier;
        }
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/SkullBlockEntityRenderer;getRenderLayer(Lnet/minecraft/block/SkullBlock$SkullType;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/client/render/RenderLayer;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void etf$alterTexture(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci, float g, BlockState blockState, boolean bl, Direction direction, int k, float h, SkullBlock.SkullType skullType, SkullBlockEntityModel skullBlockEntityModel) {
        entity_texture_features$thisETFTexture = null;
        entity_texture_features$thisETFPlayerTexture = null;
        etf$entity = null;
        if (ETFConfigData.enableCustomTextures && ETFConfigData.enableCustomBlockEntities && ETFConfigData.skinFeaturesEnabled) {
            World worldCheck = skullBlockEntity.getWorld();
            if (worldCheck == null) worldCheck = MinecraftClient.getInstance().world;
            if (worldCheck != null) {

                //make uuid for block based on set data
                int hash = (direction == null ? 0 : direction.hashCode()) + (bl ? 1 : 0) + skullType.hashCode();

                Identifier identifier = etf$getIdentifier(skullType, skullBlockEntity.getOwner());
                boolean player = skullBlockEntity.getOwner() != null;
                if (player) {
                    ETFPlayerEntity etfPlayer = new ETFPlayerHeadWrapper(skullBlockEntity, hash + skullBlockEntity.getOwner().getId().hashCode());
                    etf$entity = etfPlayer;
                    entity_texture_features$thisETFPlayerTexture = ETFManager.getInstance().getPlayerTexture(etfPlayer, identifier);
                } else {
                    etf$entity = new ETFBlockEntityWrapper(skullBlockEntity, hash);
                    entity_texture_features$thisETFTexture = ETFManager.getInstance().getETFTexture(identifier, etf$entity, ETFManager.TextureSource.BLOCK_ENTITY, true);
                }
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void etf$renderFeatures(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci, float g, BlockState blockState, boolean bl, Direction direction, int k, float h, SkullBlock.SkullType skullType, SkullBlockEntityModel skullBlockEntityModel, RenderLayer renderLayer) {
        if (ETFConfigData.enableEmissiveBlockEntities && ETFConfigData.skinFeaturesEnabled) {
            //vanilla positional code copy
            matrixStack.push();
            if (direction == null) {
                matrixStack.translate(0.5F, 0.0F, 0.5F);
            } else {
                matrixStack.translate(0.5F - (float) direction.getOffsetX() * 0.25F, 0.25F, 0.5F - (float) direction.getOffsetZ() * 0.25F);
            }
            matrixStack.scale(-1.0F, -1.0F, 1.0F);
            skullBlockEntityModel.setHeadRotation(g, h, 0.0F);
            //vanilla end

            if (entity_texture_features$thisETFTexture != null) {
                entity_texture_features$thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, skullBlockEntityModel, ETFManager.EmissiveRenderModes.DULL);
            } else if (entity_texture_features$thisETFPlayerTexture != null) {
                entity_texture_features$thisETFPlayerTexture.renderFeatures(matrixStack, vertexConsumerProvider, i, skullBlockEntityModel);
            }
            matrixStack.pop();
        }

    }

    @ModifyArg(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/SkullBlockEntityRenderer;renderSkull(Lnet/minecraft/util/math/Direction;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/block/entity/SkullBlockEntityModel;Lnet/minecraft/client/render/RenderLayer;)V")
            , index = 7)
    private RenderLayer etf$modifyRenderLayer(RenderLayer renderLayer) {
        if (entity_texture_features$thisETFTexture != null) {
            return RenderLayer.getEntityCutoutNoCullZOffset(entity_texture_features$thisETFTexture.getTextureIdentifier(etf$entity));
        } else if (entity_texture_features$thisETFPlayerTexture != null) {
            Identifier skin = entity_texture_features$thisETFPlayerTexture.getBaseHeadTextureIdentifierOrNullForVanilla();
            if (skin != null)
                return RenderLayer.getEntityTranslucent(skin);
        }
        return renderLayer;
    }


}


