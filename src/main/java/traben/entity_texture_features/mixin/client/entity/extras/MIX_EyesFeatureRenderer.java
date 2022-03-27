package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_texture_features.client.ETF_METHODS;

import java.util.UUID;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(EyesFeatureRenderer.class)
public abstract class MIX_EyesFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> implements ETF_METHODS {


    public MIX_EyesFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }


    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void mixin(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {

        Identifier check = this.getAlteredEyesTexture((LivingEntity) entity);
        if (ETF_isExistingFile(check)) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEyes(check));
            this.getContextModel().render(matrices, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            ci.cancel();
        }
    }


    private Identifier getAlteredEyesTexture(LivingEntity entity) {
        String path = "";
        if (entity instanceof EndermanEntity) {
            path = "textures/entity/enderman/enderman_eyes.png";
        } else if (entity instanceof SpiderEntity) {
            path = "textures/entity/spider_eyes.png";
        } else if (entity instanceof PhantomEntity) {
            path = "textures/entity/phantom_eyes.png";
        }
        Identifier vanilla = new Identifier(path);
        UUID id = entity.getUuid();

        try {
            if (!Texture_OptifineOrTrueRandom.containsKey(path)) {
                ETF_processNewRandomTextureCandidate(path);
            }
            if (Texture_OptifineOrTrueRandom.containsKey(path)) {
                //if needs to check if change required
                if (UUID_entityAwaitingDataClearing2.containsKey(id)) {
                    if (UUID_randomTextureSuffix2.containsKey(id)) {
                        if (!hasUpdatableRandomCases2.containsKey(id)) {
                            hasUpdatableRandomCases2.put(id, true);
                        }
                        if (hasUpdatableRandomCases2.get(id)) {
                            //skip a few ticks
                            //UUID_entityAwaitingDataClearing.put(id, UUID_entityAwaitingDataClearing.get(id)+1);
                            if (UUID_entityAwaitingDataClearing2.get(id) + 100 < System.currentTimeMillis()) {
                                if (Texture_OptifineOrTrueRandom.get(path)) {
                                    //if (UUID_randomTextureSuffix.containsKey(id)) {
                                    int hold = UUID_randomTextureSuffix2.get(id);
                                    ETF_resetSingleData(id);
                                    ETF_testCases(path, id, entity, true, UUID_randomTextureSuffix2, hasUpdatableRandomCases2);
                                    //if didnt change keep the same
                                    if (!UUID_randomTextureSuffix2.containsKey(id)) {
                                        UUID_randomTextureSuffix2.put(id, hold);
                                    }
                                    //}
                                }//else here would do something for true random but no need really - may optimise this

                                UUID_entityAwaitingDataClearing2.remove(id);
                            }
                        } else {
                            UUID_entityAwaitingDataClearing2.remove(id);
                        }
                    }

                }
                if (Texture_OptifineOrTrueRandom.get(path)) {//optifine random
                    //if it doesn't have a random already assign one
                    if (!UUID_randomTextureSuffix2.containsKey(id)) {
                        ETF_testCases(path, id, entity, false, UUID_randomTextureSuffix2, hasUpdatableRandomCases2);
                        //if all failed set to vanilla
                        if (!UUID_randomTextureSuffix2.containsKey(id)) {
                            UUID_randomTextureSuffix2.put(id, 0);
                        }
                        UUID_entityAlreadyCalculated.add(id);
                    }
                    // System.out.println("suffix was ="+UUID_randomTextureSuffix.get(id));
                    if (UUID_randomTextureSuffix2.get(id) == 0) {
                        if (!TEXTURE_HasOptifineDefaultReplacement.containsKey(vanilla.toString())) {
                            TEXTURE_HasOptifineDefaultReplacement.put(vanilla.toString(), ETF_isExistingFile(ETF_returnOptifineOrVanillaIdentifier(path)));
                        }
                        if (TEXTURE_HasOptifineDefaultReplacement.get(vanilla.toString())) {
                            return ETF_returnBlinkIdOrGiven(entity, ETF_returnOptifineOrVanillaIdentifier(path).toString(), id);
                        } else {
                            return ETF_returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                        }

                    } else {
                        return ETF_returnBlinkIdOrGiven(entity, ETF_returnOptifineOrVanillaIdentifier(path, UUID_randomTextureSuffix2.get(id)).toString(), id);
                    }

                } else {//true random assign
                    hasUpdatableRandomCases2.put(id, false);
                    if (Texture_TotalTrueRandom.get(path) > 0) {
                        if (!UUID_randomTextureSuffix2.containsKey(id)) {
                            int randomReliable = Math.abs(id.hashCode());
                            randomReliable %= Texture_TotalTrueRandom.get(path);
                            randomReliable++;
                            if (randomReliable == 1 && ignoreOnePNG.get(path)) {
                                randomReliable = 0;
                            }
                            UUID_randomTextureSuffix2.put(id, randomReliable);
                            UUID_entityAlreadyCalculated.add(id);
                        }
                        if (UUID_randomTextureSuffix2.get(id) == 0) {
                            return ETF_returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                        } else {
                            return ETF_returnBlinkIdOrGiven(entity, ETF_returnOptifineOrVanillaPath(path, UUID_randomTextureSuffix2.get(id), ""), id);
                        }
                    } else {
                        return ETF_returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                    }
                }
            } else {
                ETF_modMessage("not random", false);
                return ETF_returnBlinkIdOrGiven(entity, vanilla.toString(), id);
            }

        } catch (Exception e) {
            ETF_modMessage(e.toString(), false);
            return ETF_returnBlinkIdOrGiven(entity, vanilla.toString(), id);
        }
    }

}
