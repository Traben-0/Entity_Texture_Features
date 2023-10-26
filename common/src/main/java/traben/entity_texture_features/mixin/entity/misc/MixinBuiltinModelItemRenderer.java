package traben.entity_texture_features.mixin.entity.misc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
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
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.texture_features.ETFManager;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

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
    private void etf$changeTexture(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        //at this point trident has already rendered we just have to render alterations over it :/

        if (stack.isOf(Items.TRIDENT)) {
            boolean tridentOveridden = false;
            if (ETFConfigData.enableTridents && ETFConfigData.enableCustomTextures) {
                if (stack.hasCustomName()) {
                    String path = TridentEntityModel.TEXTURE.toString();
                    String name = stack.getName().getString().replaceAll(" ", "_").toLowerCase().replaceAll("[^a-z\\d/_.-]", "");
                    Identifier possibleId = new Identifier(path.replace(".png", "_" + name + ".png"));
                    if (!ETFManager.getInstance().DOES_IDENTIFIER_EXIST_CACHED_RESULT.containsKey(possibleId)) {
                        ETFManager.getInstance().DOES_IDENTIFIER_EXIST_CACHED_RESULT.put(possibleId, MinecraftClient.getInstance().getResourceManager().getResource(possibleId).isPresent());
                    }
                    if (ETFManager.getInstance().DOES_IDENTIFIER_EXIST_CACHED_RESULT.getBoolean(possibleId)) {
                        matrices.push();
                        matrices.scale(1.0F, -1.0F, -1.0F);
                        VertexConsumer block = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.modelTrident.getLayer(possibleId), false, stack.hasGlint());
                        this.modelTrident.render(matrices, block, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrices.pop();
                        tridentOveridden = true;
                    } //vanilla
                }  //vanilla
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
                String name = stack.hasCustomName() ? "_" + stack.getName().getString().trim().replaceAll(" ", "_").toLowerCase().replaceAll("[^a-z\\d/_.-]", "") : "";
                Identifier file = new Identifier(path.replace(".png", name + "_e.png"));
                if (!ETFManager.getInstance().DOES_IDENTIFIER_EXIST_CACHED_RESULT.containsKey(file)) {
                    ETFManager.getInstance().DOES_IDENTIFIER_EXIST_CACHED_RESULT.put(file, MinecraftClient.getInstance().getResourceManager().getResource(file).isPresent());
                }
                if (ETFManager.getInstance().DOES_IDENTIFIER_EXIST_CACHED_RESULT.getBoolean(file)) {
                    matrices.push();
                    matrices.scale(1.0F, -1.0F, -1.0F);
                    VertexConsumer consumer = vertexConsumers.getBuffer(
                            ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT ?
                                    RenderLayer.getBeaconBeam(file, true) :
                                    RenderLayer.getEntityTranslucent(file));
                    this.modelTrident.render(matrices, consumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, 1, 1, 1, 1);
                    //ETFUtils.generalEmissiveRenderModel(matrices, vertexConsumers, fileString, this.modelTrident);
                    matrices.pop();

                }
            }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        } else if (stack.isOf(Items.SHIELD)) {
            if (ETFConfigData.specialEmissiveShield && ETFConfigData.enableEmissiveTextures) {

                boolean bl = BlockItem.getBlockEntityNbt(stack) != null;
                Identifier file = new Identifier(bl ? "textures/entity/shield_base_e.png" : "textures/entity/shield_base_nopattern_e.png");
                if (!ETFManager.getInstance().DOES_IDENTIFIER_EXIST_CACHED_RESULT.containsKey(file)) {
                    ETFManager.getInstance().DOES_IDENTIFIER_EXIST_CACHED_RESULT.put(file, MinecraftClient.getInstance().getResourceManager().getResource(file).isPresent());
                }
                if (ETFManager.getInstance().DOES_IDENTIFIER_EXIST_CACHED_RESULT.getBoolean(file)) {
                    matrices.push();
                    matrices.scale(1.0F, -1.0F, -1.0F);
                    VertexConsumer consumer = vertexConsumers.getBuffer(
                            ETFManager.getEmissiveMode() == ETFManager.EmissiveRenderModes.BRIGHT ?
                                    RenderLayer.getBeaconBeam(file, true) :
                                    RenderLayer.getEntityTranslucent(file));

                    //ETFUtils.generalEmissiveRenderPart(matrices, vertexConsumers, fileString, modelShield.getHandle(), false);
                    modelShield.getHandle().render(matrices, consumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, 1, 1, 1, 1);
                    modelShield.render(matrices, consumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, 1, 1, 1, 1);

                    //ETFUtils.generalEmissiveRenderPart(matrices, vertexConsumers, fileString, modelShield.getPlate(), false);
                    if (!bl)
                        modelShield.getPlate().render(matrices, consumer, ETFClientCommon.EMISSIVE_FEATURE_LIGHT_VALUE, overlay, 1, 1, 1, 1);
                    //todo banner patterns implementation
                    matrices.pop();
                }

            }

        }
    }


}


