package traben.entity_texture_features.mixin.client.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.entity_texture_features_METHODS;

import java.util.ArrayList;
import java.util.UUID;

import static traben.entity_texture_features.client.entity_texture_features_CLIENT.*;

@Mixin(LivingEntityRenderer.class)
public abstract class MIX_LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M>, entity_texture_features_METHODS {
    protected MIX_LivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }


    //  [0] = total randoms, [1] = self random


    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", shift = At.Shift.AFTER))
    private void applyEmissive(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        UUID id = livingEntity.getUuid();
        String fileString = getTexture(livingEntity).getPath();
        //System.out.println(fileString);
        if (Texture_TotalRandom.containsKey(fileString)) {
            if (UUID_randomTextureSuffix.get(id) !=0) {
                fileString = fileString.replace(".png", UUID_randomTextureSuffix.get(id) + ".png");
            }
        }
        if (Texture_Emissive.containsKey(fileString)) {
            if (Texture_Emissive.get(fileString) != null) {
                VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getEyes(Texture_Emissive.get(fileString)));

                this.getModel().render(matrixStack
                        , textureVert
                        , 15728640
                        , OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        } else {//creates and sets emissive for texture if it exists
            Identifier fileName_e = new Identifier(fileString.replace(".png", emissiveSuffix+".png"));
            if (isExistingFile(MinecraftClient.getInstance().getResourceManager(), fileName_e)) {
                VertexConsumer textureVert = vertexConsumerProvider.getBuffer(RenderLayer.getEyes(fileName_e));
                Texture_Emissive.put(fileString, fileName_e);
                this.getModel().render(matrixStack, textureVert, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                Texture_Emissive.put(fileString, null);
            }
        }
    }


    @Redirect(
            method = "getRenderLayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/entity/Entity;)Lnet/minecraft/util/Identifier;"))
    private Identifier returnOwnRandomTexture(LivingEntityRenderer instance, Entity entity) {
        Identifier vanilla = getTexture((T) entity);
        UUID id = entity.getUuid();

        if (!Texture_TotalRandom.containsKey(vanilla.getPath())) {
            processNewRandomTextureCandidate(vanilla.getPath(), entity);
        }

        if (Texture_TotalRandom.get(vanilla)>0) {
            if (!UUID_randomTextureSuffix.containsKey(id)){
                put in randomizer method
                    int randomReliable = id.hashCode() > 0 ? id.hashCode() : -id.hashCode();
                    randomReliable %= Texture_TotalRandom.get(vanilla)+1;
                    UUID_randomTextureSuffix.put(id,randomReliable );
            }
            if (UUID_randomTextureSuffix.get(id)==0){
                return vanilla;
            }else {
                return new Identifier(vanilla.getPath().replace(".png", UUID_randomTextureSuffix.get(id) + ".png"));
            }
        } else {
            return vanilla;
        }

    }




}


