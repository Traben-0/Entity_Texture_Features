package traben.entity_texture_features.mixin.client.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.entity_texture_features_METHODS;

import java.util.*;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.hasEmissive;
import static traben.entity_texture_features.client.entity_texture_features_CLIENT.randomData;

@Mixin(LivingEntityRenderer.class)
public abstract class MIX_LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M>, entity_texture_features_METHODS {
    protected MIX_LivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }


    //  [0] = total randoms, [1] = self random


    @Inject( method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",shift = At.Shift.AFTER))
    private void applyEmissive(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci)  {
        UUID id = livingEntity.getUuid();
        String fileString = getTexture(livingEntity).getPath();
        if (randomData.get(id)[1] > 1) {
            fileString = fileString.replace(".png", randomData.get(id)[1] + ".png");
        }
        if (hasEmissive.containsKey(fileString)) {
            if (hasEmissive.get(fileString) !=null){
                VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getEyes(hasEmissive.get(fileString)));

                this.getModel().render(matrixStack
                        , textureVert
                        , 15728640
                        , OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }else{
            // if (!randomData.containsKey(livingEntity.getUuid())) {
            //     System.out.println("no data - making");
            //     fileString = setRandom(resources,fileString,livingEntity.getUuid());
            // } else

            //ResourceManager resources = MinecraftClient.getInstance().getResourceManager();

            Identifier fileName_e = new Identifier(fileString.replace(".png", "_e.png"));
            if (isExistingFile(MinecraftClient.getInstance().getResourceManager(), fileName_e)) {
                VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getEyes(fileName_e));
                hasEmissive.put(fileString,fileName_e);
                this.getModel().render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }else{
                hasEmissive.put(fileString,null);
            }
        }
    }


    @Redirect(
            method = "getRenderLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier returnOwnRandomTexture(LivingEntityRenderer instance, Entity entity) {
        Identifier vanilla = getTexture((T)entity);

        if (!randomData.containsKey(entity.getUuid())) {
            System.out.println("no data - making2");
            setRandom(MinecraftClient.getInstance().getResourceManager(),vanilla.getPath(),entity.getUuid());
        }
        if (randomData.get(entity.getUuid())[1] > 1) {
            return new Identifier(vanilla.getPath().replace(".png", randomData.get(entity.getUuid())[1]+".png"));
        }else{
            return vanilla;
        }

    }


    private String setRandom(ResourceManager resourceManager, String vanillaPath, UUID id){
        boolean keepGoing = true;
        String checkPath;
        int count = 1;
        while (keepGoing){
            count++;
            checkPath = vanillaPath.replace(".png",(count+".png"));
            keepGoing = isExistingFile(resourceManager,new Identifier(checkPath));
        }
        System.out.println("count="+(count-1));
        Integer[] data = {count-1,1};
        //this.randomAmount = count-1;
        //0=vanilla, 1+ = randoms
        if (data[0] != 1){
            data[1] = (new Random()).nextInt(count-1)+1;
            randomData.put(id,data);
            return vanillaPath.replace(".png",(data[1]+".png"));
        }
        else{
            randomData.put(id,data);
            return vanillaPath;
        }
    }



}


