package traben.entity_texture_features.mixin.entity.block_entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
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
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFPlayerTexture;
import traben.entity_texture_features.texture_handlers.ETFTexture;
import traben.entity_texture_features.utils.ETFBlockEntityWrapper;

import java.util.Map;
import java.util.UUID;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(SkullBlockEntityRenderer.class)
public abstract class MixinSkullBlockEntityRenderer implements BlockEntityRenderer<BedBlockEntity> {


    @Shadow @Final
    private static Map<SkullBlock.SkullType, Identifier> TEXTURES;
    private ETFTexture thisETFTexture = null;
    private ETFPlayerTexture thisETFPlayerTexture = null;
    private ETFBlockEntityWrapper etf$entity = null;

    @Inject(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/SkullBlockEntityRenderer;getRenderLayer(Lnet/minecraft/block/SkullBlock$SkullType;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/client/render/RenderLayer;"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void etf$alterTexture(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci, float g, BlockState blockState, boolean bl, Direction direction, int k, float h, SkullBlock.SkullType skullType, SkullBlockEntityModel skullBlockEntityModel) {
        thisETFTexture = null;
        thisETFPlayerTexture = null;
        etf$entity = null;
        if (ETFConfigData.enableCustomTextures && ETFConfigData.enableCustomBlockEntities) {
            World worldCheck = skullBlockEntity.getWorld();
            if (worldCheck == null) worldCheck = MinecraftClient.getInstance().world;
            if (worldCheck != null) {

                boolean player = skullBlockEntity.getOwner() != null;
                UUID uuid = player ? skullBlockEntity.getOwner().getId() :  UUID.nameUUIDFromBytes((skullBlockEntity.getPos().toString() + skullBlockEntity.getType().toString()).getBytes());

                etf$entity = new ETFBlockEntityWrapper(skullBlockEntity, uuid);
                Identifier identifier = etf$getIdentifier(skullType,skullBlockEntity.getOwner());

                if(player){
                    thisETFPlayerTexture = ETFManager.getInstance().getPlayerHeadTexture(etf$entity);
                }else{
                    thisETFTexture = ETFManager.getInstance().getETFTexture(identifier, etf$entity, ETFManager.TextureSource.BLOCK_ENTITY,true);
                }
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private void etf$alterTexture(SkullBlockEntity skullBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci, float g, BlockState blockState, boolean bl, Direction direction, int k, float h, SkullBlock.SkullType skullType, SkullBlockEntityModel skullBlockEntityModel, RenderLayer renderLayer) {
        if (ETFConfigData.enableEmissiveBlockEntities) {
            //vanilla positional code copy
            matrixStack.push();
            if (direction == null) {
                matrixStack.translate(0.5F, 0.0F, 0.5F);
            } else {
                matrixStack.translate(0.5F - (float)direction.getOffsetX() * 0.25F, 0.25F, 0.5F - (float)direction.getOffsetZ() * 0.25F);
            }
            matrixStack.scale(-1.0F, -1.0F, 1.0F);
            skullBlockEntityModel.setHeadRotation(g, h, 0.0F);
            //vanilla end

            if (thisETFTexture != null) {
                thisETFTexture.renderEmissive(matrixStack, vertexConsumerProvider, skullBlockEntityModel, ETFManager.EmissiveRenderModes.blockEntityMode());
            } else if (thisETFPlayerTexture != null) {
                thisETFPlayerTexture.renderFeatures(matrixStack, vertexConsumerProvider, i, skullBlockEntityModel);
            }
            matrixStack.pop();
        }

    }

    @ModifyArg(method = "render(Lnet/minecraft/block/entity/SkullBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/SkullBlockEntityRenderer;renderSkull(Lnet/minecraft/util/math/Direction;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/block/entity/SkullBlockEntityModel;Lnet/minecraft/client/render/RenderLayer;)V")
            ,index = 7)
    private RenderLayer etf$modifyRenderLayer(RenderLayer renderLayer){
        if(thisETFTexture != null){
            return RenderLayer.getEntityCutoutNoCullZOffset(thisETFTexture.getTextureIdentifier(etf$entity));
        }else if(thisETFPlayerTexture != null){
            Identifier skin = thisETFPlayerTexture.getBaseHeadTextureIdentifierOrNullForVanilla();
            if(skin != null)
                return RenderLayer.getEntityTranslucent(skin);
        }
        return renderLayer;
    }


    private static Identifier etf$getIdentifier(SkullBlock.SkullType type, @Nullable GameProfile profile) {
        Identifier identifier = TEXTURES.get(type);
        if (type == SkullBlock.Type.PLAYER && profile != null) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(profile);
            return map.containsKey(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN) ? minecraftClient.getSkinProvider().loadSkin((MinecraftProfileTexture)map.get(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN), com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN) : DefaultSkinHelper.getTexture(Uuids.getUuidFromProfile(profile));
        } else {
            return identifier;
        }
    }


}


