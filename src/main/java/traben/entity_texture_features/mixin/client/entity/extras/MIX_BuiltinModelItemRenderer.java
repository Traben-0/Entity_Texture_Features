package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
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
import traben.entity_texture_features.client.ETF_METHODS;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class MIX_BuiltinModelItemRenderer implements SynchronousResourceReloader, ETF_METHODS {
    @Shadow
    private TridentEntityModel modelTrident;

    //first cancel vanilla render
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"), index = 0)
    private Item ETF_injected(Item item) {
        if (item == Items.TRIDENT) {
            //this will automatically fail as blocks do not get processed here
            return Items.DIRT;
        } else {
            return item;
        }
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void ETF_changeElytraTexture(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        //at this point trident has already rendered we just have to render alterations over it :/
        if (stack.isOf(Items.TRIDENT) && ETF_ConfigData.enableTridents) {
            if (ETF_ConfigData.enableCustomTextures) {
                if (stack.hasCustomName()) {
                    String path = TridentEntityModel.TEXTURE.toString();
                    String name = stack.getName().getString().toLowerCase().replaceAll("[^a-z0-9/_.-]", "");
                    Identifier possibleId = new Identifier(path.replace(".png", "_" + name + ".png"));
                    if (ETF_isExistingFile(possibleId)) {
                        matrices.push();
                        matrices.scale(1.0F, -1.0F, -1.0F);
                        VertexConsumer block = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.modelTrident.getLayer(possibleId), false, stack.hasGlint());
                        this.modelTrident.render(matrices, block, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrices.pop();
                    } else {//vanilla
                        matrices.push();
                        matrices.scale(1.0F, -1.0F, -1.0F);
                        VertexConsumer block = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, stack.hasGlint());
                        this.modelTrident.render(matrices, block, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
                        matrices.pop();
                    }//vanilla render
                } else {//vanilla
                    matrices.push();
                    matrices.scale(1.0F, -1.0F, -1.0F);
                    VertexConsumer block = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, stack.hasGlint());
                    this.modelTrident.render(matrices, block, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
                    matrices.pop();
                }
            } else {//vanilla
                matrices.push();
                matrices.scale(1.0F, -1.0F, -1.0F);
                VertexConsumer block = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, stack.hasGlint());
                this.modelTrident.render(matrices, block, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
                matrices.pop();
            }
            if (ETF_ConfigData.enableEmissiveTextures) {
                String path = TridentEntityModel.TEXTURE.toString();
                String name = stack.hasCustomName() ? "_" + stack.getName().getString().toLowerCase().replaceAll("[^a-z0-9/_.-]", "") : "";
                String fileString = path.replace(".png", "_" + name + ".png");
                if (!ETF_PATH_EmissiveTextureIdentifier.containsKey(fileString)) {
                    for (String suffix :
                            ETF_emissiveSuffixes) {
                        Identifier possibleId = new Identifier(path.replace(".png", name + suffix + ".png"));
                        if (ETF_isExistingFile(possibleId)) {
                            ETF_PATH_EmissiveTextureIdentifier.put(fileString, possibleId);
                        }
                    }
                    if (!ETF_PATH_EmissiveTextureIdentifier.containsKey(fileString)) {
                        ETF_PATH_EmissiveTextureIdentifier.put(fileString, null);
                    }
                }
                if (ETF_PATH_EmissiveTextureIdentifier.get(fileString) != null) {
                    matrices.push();
                    matrices.scale(1.0F, -1.0F, -1.0F);
                    VertexConsumer block = vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(ETF_PATH_EmissiveTextureIdentifier.get(fileString), true));
                    this.modelTrident.render(matrices, block, 15728640, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
                    matrices.pop();


                }
            }
        }
    }
}


