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
        System.out.println(fileString);
        if (UUID_isRandom.get(id)) {
            //fileString = fileString.replace(".png", randomData.get(id) + ".png");
            fileString = UUID_randomTexture.get(id).getPath();
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

        //spread out randomly for lag prevention
        //reset to detect if texture has changed in base mob and update random assignment
        if ( (entity instanceof GhastEntity || entity instanceof VexEntity)
                && ((LivingEntity) entity).getRandom().nextInt(10) == 0
        ) {//more frequent for hostiles
            resetSingleVisuals(entity.getUuid());
        }else if ((entity instanceof WolfEntity || entity instanceof BeeEntity|| entity instanceof FoxEntity) && ((LivingEntity) entity).getRandom().nextInt(64) == 0
        ) {
            resetSingleVisuals(entity.getUuid());
        }
        //checks if random needs to be changed
        if (!UUID_isRandom.containsKey(entity.getUuid())){
            //System.out.println("no data - making2");
            setRandom(MinecraftClient.getInstance().getResourceManager(), vanilla.getPath(), entity.getUuid());
        }
        if (UUID_isRandom.get(entity.getUuid())) {
//                //if(UUID_randomTexture.get(entity.getUuid())[0].getPath().equals(vanilla.getPath()) ){
//                String[] nameOfFile = vanilla.getPath().replace(".png", "").split("/");
//                String[] randomToCheck = UUID_randomTexture.get(entity.getUuid()).getPath().replace(".png", "").split("/");
//                if (randomToCheck[randomToCheck.length-1].replaceAll("[0-9]","").equals(nameOfFile[nameOfFile.length - 1])) {
//                    UUID_randomTexture.remove(entity.getUuid());
//                    UUID_isRandom.remove(entity.getUuid());
//                    setRandom(MinecraftClient.getInstance().getResourceManager(), vanilla.getPath(), entity.getUuid());
            return UUID_randomTexture.get(entity.getUuid());
        } else {
            return vanilla;
        }

    }


    private String setRandom(ResourceManager resourceManager, String vanillaPath, UUID id) {
        boolean keepGoing = false;
        ArrayList<String> allTextures = new ArrayList<String>();
        String checkPath;
        String checkPathOptifineFormat;
        String checkPathOldRandomFormat;
        boolean isOptifine = false;
        boolean oldRandom = false;

        //first iteration longer
        int count = 1;
        allTextures.add(vanillaPath);
        //can start from either texture1.png or texture2.png check both first
        //check if texturename1.png is used
        checkPath = vanillaPath.replace(".png", (count + ".png"));
        checkPathOldRandomFormat = vanillaPath.replace(".png", (count + ".png")).replace("textures/entity", "optifine/mob");
        checkPathOptifineFormat = vanillaPath.replace(".png", (count + ".png")).replace("textures", "optifine/random");
        if (isExistingFile(resourceManager, new Identifier(checkPath))) {
            allTextures.add(checkPath);
        } else if (isExistingFile(resourceManager, new Identifier(checkPathOptifineFormat))) {
            isOptifine = true;
            allTextures.add(checkPathOptifineFormat);
        } else if (isExistingFile(resourceManager, new Identifier(checkPathOldRandomFormat))) {
            oldRandom = true;
            allTextures.add(checkPathOldRandomFormat);
        }
        count++;
        //check if texture 2.png is used
        checkPath = vanillaPath.replace(".png", (count + ".png"));
        checkPathOldRandomFormat = vanillaPath.replace(".png", (count + ".png")).replace("textures/entity", "optifine/mob");
        checkPathOptifineFormat = vanillaPath.replace(".png", (count + ".png")).replace("textures", "optifine/random");
        if (isExistingFile(resourceManager, new Identifier(checkPath))) {
            allTextures.add(checkPath);
            keepGoing = true;
        } else if (isExistingFile(resourceManager, new Identifier(checkPathOptifineFormat))) {
            isOptifine = true;
            keepGoing = true;
            allTextures.add(checkPathOptifineFormat);
        } else if (isExistingFile(resourceManager, new Identifier(checkPathOldRandomFormat))) {
            oldRandom = true;
            keepGoing = true;
            allTextures.add(checkPathOldRandomFormat);
        }
        //texture3.png and further optimized iterations
        while (keepGoing) {
            count++;
            if (isOptifine) {
                checkPath = vanillaPath.replace(".png", (count + ".png")).replace("textures", "optifine/random");
            } else if (oldRandom) {
                checkPath = vanillaPath.replace(".png", (count + ".png")).replace("textures/entity", "optifine/mob");
            } else {
                checkPath = vanillaPath.replace(".png", (count + ".png"));
            }

            if (isExistingFile(resourceManager, new Identifier(checkPath))) {
                allTextures.add(checkPath);
            } else {
                keepGoing = false;
            }

        }

        if (allTextures.size() > 1) {

            int randomReliable = id.hashCode() > 0 ? id.hashCode() : -id.hashCode();
            randomReliable %= allTextures.size();

            UUID_isRandom.put(id, true);
            //Identifier[] toSend = {new Identifier(vanillaPath),new Identifier(allTextures.get(randomReliable))};
             UUID_randomTexture.put(id,new Identifier(allTextures.get(randomReliable)) );
            return allTextures.get(randomReliable);
        } else {
            UUID_isRandom.put(id, false);
            return vanillaPath;
        }
    }


}


