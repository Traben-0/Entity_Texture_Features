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
import traben.entity_texture_features.client.ETFUtils;

import java.util.UUID;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(EyesFeatureRenderer.class)
public abstract class MixinEyesFeatureRenderer<T extends Entity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {


    public MixinEyesFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }


    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void etf$mixin(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {

        Identifier check = this.etf$getAlteredEyesTexture((LivingEntity) entity);
        if (ETFUtils.etf$isExistingNativeImageFile(check)) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEyes(check));
            this.getContextModel().render(matrices, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            ci.cancel();
        }
    }


    private Identifier etf$getAlteredEyesTexture(LivingEntity entity) {
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
            if (!etf$PATH_OptifineOrTrueRandom.containsKey(path)) {
                ETFUtils.etf$processNewRandomTextureCandidate(path);
            }
            if (etf$PATH_OptifineOrTrueRandom.containsKey(path)) {
                //if needs to check if change required
                if (etf$UUID_entityAwaitingDataClearing2.containsKey(id)) {
                    if (etf$UUID_randomTextureSuffix2.containsKey(id)) {
                        if (!etf$UUID_hasUpdatableRandomCases2.containsKey(id)) {
                            etf$UUID_hasUpdatableRandomCases2.put(id, true);
                        }
                        if (etf$UUID_hasUpdatableRandomCases2.get(id)) {
                            //skip a few ticks
                            //UUID_entityAwaitingDataClearing.put(id, UUID_entityAwaitingDataClearing.get(id)+1);
                            if (etf$UUID_entityAwaitingDataClearing2.get(id) + 100 < System.currentTimeMillis()) {
                                if (etf$PATH_OptifineOrTrueRandom.get(path)) {
                                    //if (UUID_randomTextureSuffix.containsKey(id)) {
                                    int hold = etf$UUID_randomTextureSuffix2.get(id);
                                    ETFUtils.etf$resetSingleData(id);
                                    ETFUtils.etf$testCases(path, id, entity, true, etf$UUID_randomTextureSuffix2, etf$UUID_hasUpdatableRandomCases2);
                                    //if didnt change keep the same
                                    if (!etf$UUID_randomTextureSuffix2.containsKey(id)) {
                                        etf$UUID_randomTextureSuffix2.put(id, hold);
                                    }
                                    //}
                                }//else here would do something for true random but no need really - may optimise this

                                etf$UUID_entityAwaitingDataClearing2.remove(id);
                            }
                        } else {
                            etf$UUID_entityAwaitingDataClearing2.remove(id);
                        }
                    }

                }
                if (etf$PATH_OptifineOrTrueRandom.get(path)) {//optifine random
                    //if it doesn't have a random already assign one
                    if (!etf$UUID_randomTextureSuffix2.containsKey(id)) {
                        ETFUtils.etf$testCases(path, id, entity, false, etf$UUID_randomTextureSuffix2, etf$UUID_hasUpdatableRandomCases2);
                        //if all failed set to vanilla
                        if (!etf$UUID_randomTextureSuffix2.containsKey(id)) {
                            etf$UUID_randomTextureSuffix2.put(id, 0);
                        }
                        etf$UUID_entityAlreadyCalculated.add(id);
                    }
                    // System.out.println("suffix was ="+UUID_randomTextureSuffix.get(id));
                    if (etf$UUID_randomTextureSuffix2.get(id) == 0) {
                        if (!etf$PATH_HasOptifineDefaultReplacement.containsKey(vanilla.toString())) {
                            etf$PATH_HasOptifineDefaultReplacement.put(vanilla.toString(), ETFUtils.etf$isExistingNativeImageFile(ETFUtils.etf$returnOptifineOrVanillaIdentifier(path)));
                        }
                        if (etf$PATH_HasOptifineDefaultReplacement.get(vanilla.toString())) {
                            return ETFUtils.etf$returnBlinkIdOrGiven(entity, ETFUtils.etf$returnOptifineOrVanillaIdentifier(path).toString(), id);
                        } else {
                            return ETFUtils.etf$returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                        }

                    } else {
                        return ETFUtils.etf$returnBlinkIdOrGiven(entity, ETFUtils.etf$returnOptifineOrVanillaIdentifier(path, etf$UUID_randomTextureSuffix2.get(id)).toString(), id);
                    }

                } else {//true random assign
                    etf$UUID_hasUpdatableRandomCases2.put(id, false);
                    if (etf$PATH_TotalTrueRandom.get(path) > 0) {
                        if (!etf$UUID_randomTextureSuffix2.containsKey(id)) {
                            int randomReliable = Math.abs(id.hashCode());
                            randomReliable %= etf$PATH_TotalTrueRandom.get(path);
                            randomReliable++;
                            if (randomReliable == 1 && etf$PATH_ignoreOnePNG.get(path)) {
                                randomReliable = 0;
                            }
                            etf$UUID_randomTextureSuffix2.put(id, randomReliable);
                            etf$UUID_entityAlreadyCalculated.add(id);
                        }
                        if (etf$UUID_randomTextureSuffix2.get(id) == 0) {
                            return ETFUtils.etf$returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                        } else {
                            return ETFUtils.etf$returnBlinkIdOrGiven(entity, ETFUtils.etf$returnOptifineOrVanillaPath(path, etf$UUID_randomTextureSuffix2.get(id), ""), id);
                        }
                    } else {
                        return ETFUtils.etf$returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                    }
                }
            } else {
                ETFUtils.etf$modMessage("not random", false);
                return ETFUtils.etf$returnBlinkIdOrGiven(entity, vanilla.toString(), id);
            }

        } catch (Exception e) {
            ETFUtils.etf$modMessage(e.toString(), false);
            return ETFUtils.etf$returnBlinkIdOrGiven(entity, vanilla.toString(), id);
        }
    }

}
