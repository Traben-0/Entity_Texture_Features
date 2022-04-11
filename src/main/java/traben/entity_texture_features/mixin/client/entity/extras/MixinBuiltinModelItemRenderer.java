package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETFUtils;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class MixinBuiltinModelItemRenderer implements SynchronousResourceReloader {
    @Shadow
    private TridentEntityModel modelTrident;
    @Shadow
    private ShieldEntityModel modelShield;

    //first cancel vanilla render
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"), index = 0)
    private Item etf$injected(Item item) {
        if (item == Items.TRIDENT) {
            //this will automatically fail as blocks do not get processed here
            return Items.DIRT;
        } else {
            return item;
        }
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void etf$changeTexture(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        //at this point trident has already rendered we just have to render alterations over it :/

        if (stack.isOf(Items.TRIDENT)) {
            boolean tridentOveridden = false;
            if (ETFConfigData.enableTridents && ETFConfigData.enableCustomTextures) {
                if (stack.hasCustomName()) {
                    String path = TridentEntityModel.TEXTURE.toString();
                    String name = stack.getName().getString().replaceAll("\s", "_").toLowerCase().replaceAll("[^a-z0-9/_.-]", "");
                    Identifier possibleId = new Identifier(path.replace(".png", "_" + name + ".png"));
                    if (ETFUtils.etf$isExistingNativeImageFile(possibleId)) {
                        matrices.push();
                        matrices.scale(1.0F, -1.0F, -1.0F);
                        VertexConsumer block = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.modelTrident.getLayer(possibleId), false, stack.hasGlint());
                        this.modelTrident.render(matrices, block, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrices.pop();
                        tridentOveridden = true;
                    } else {//vanilla
                        //tridentOveridden = false;
                    }//vanilla render
                } else {//vanilla
                    // tridentOveridden = false;
                }
            }
            if (!tridentOveridden) {//render vanilla
                matrices.push();
                matrices.scale(1.0F, -1.0F, -1.0F);
                VertexConsumer block = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, stack.hasGlint());
                this.modelTrident.render(matrices, block, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
                matrices.pop();
            }
            if (ETFConfigData.enableTridents && ETFConfigData.enableEmissiveTextures) {
                String path = TridentEntityModel.TEXTURE.toString();
                String name = stack.hasCustomName() ? "_" + stack.getName().getString().trim().replaceAll("\s", "_").toLowerCase().replaceAll("[^a-z0-9/_.-]", "") : "";
                String fileString = path.replace(".png", name + ".png");
                matrices.push();
                matrices.scale(1.0F, -1.0F, -1.0F);
                ETFUtils.etf$GeneralEmissiveRender(matrices, vertexConsumers, fileString, this.modelTrident);
                matrices.pop();

            }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        } else if (stack.isOf(Items.SHIELD)) {
            if (ETFConfigData.specialEmissiveShield) {
                if (ETFConfigData.enableEmissiveTextures) {
                    //todo for some reason wont overlay shield in hand, yet trident does???
                    boolean bl = BlockItem.getBlockEntityNbt(stack) != null;
                    String fileString = bl ? "minecraft:textures/entity/shield_base.png" : "minecraft:textures/entity/shield_base_nopattern.png";
                    matrices.push();
                    matrices.scale(1.0F, -1.0F, -1.0F);
                    VertexConsumer consumer = ETFUtils.etf$GeneralEmissiveGetVertexConsumer(fileString, vertexConsumers);

                    ETFUtils.etf$GeneralEmissiveRenderPart(matrices, vertexConsumers, fileString, modelShield.getHandle());
                    //modelShield.getHandle().render(matrices,consumer,15728640,overlay,1,1,1,1);
                    //modelShield.render(matrices,consumer,15728640,overlay,1,1,1,1);
                    if (!bl)
                        ETFUtils.etf$GeneralEmissiveRenderPart(matrices, vertexConsumers, fileString, modelShield.getPlate());
                    //if (!bl) modelShield.getPlate().render(matrices,consumer,15728640,overlay,1,1,1,1);
                    //todo banner patterns implementation
                    matrices.pop();
                }
            }

        }
    }


}


