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
        if (ETFUtils.isExistingNativeImageFile(check)) {
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
            if (!PATH_OPTIFINE_OR_JUST_RANDOM.containsKey(path)) {
                ETFUtils.processNewRandomTextureCandidate(path);
            }
            if (PATH_OPTIFINE_OR_JUST_RANDOM.containsKey(path)) {
                //if needs to check if change required
                if (UUID_ENTITY_AWAITING_DATA_CLEARING_2.containsKey(id)) {
                    if (UUID_RANDOM_TEXTURE_SUFFIX_2.containsKey(id)) {
                        if (!UUID_HAS_UPDATABLE_RANDOM_CASES_2.containsKey(id)) {
                            UUID_HAS_UPDATABLE_RANDOM_CASES_2.put(id, true);
                        }
                        if (UUID_HAS_UPDATABLE_RANDOM_CASES_2.get(id)) {
                            //skip a few ticks
                            //UUID_entityAwaitingDataClearing.put(id, UUID_entityAwaitingDataClearing.get(id)+1);
                            if (UUID_ENTITY_AWAITING_DATA_CLEARING_2.get(id) + 100 < System.currentTimeMillis()) {
                                if (PATH_OPTIFINE_OR_JUST_RANDOM.get(path)) {
                                    //if (UUID_randomTextureSuffix.containsKey(id)) {
                                    int hold = UUID_RANDOM_TEXTURE_SUFFIX_2.get(id);
                                    ETFUtils.resetSingleData(id);
                                    ETFUtils.testCases(path, id, entity, true, UUID_RANDOM_TEXTURE_SUFFIX_2, UUID_HAS_UPDATABLE_RANDOM_CASES_2);
                                    //if didnt change keep the same
                                    if (!UUID_RANDOM_TEXTURE_SUFFIX_2.containsKey(id)) {
                                        UUID_RANDOM_TEXTURE_SUFFIX_2.put(id, hold);
                                    }
                                    //}
                                }//else here would do something for true random but no need really - may optimise this

                                UUID_ENTITY_AWAITING_DATA_CLEARING_2.remove(id);
                            }
                        } else {
                            UUID_ENTITY_AWAITING_DATA_CLEARING_2.remove(id);
                        }
                    }

                }
                if (PATH_OPTIFINE_OR_JUST_RANDOM.get(path)) {//optifine random
                    //if it doesn't have a random already assign one
                    if (!UUID_RANDOM_TEXTURE_SUFFIX_2.containsKey(id)) {
                        ETFUtils.testCases(path, id, entity, false, UUID_RANDOM_TEXTURE_SUFFIX_2, UUID_HAS_UPDATABLE_RANDOM_CASES_2);
                        //if all failed set to vanilla
                        if (!UUID_RANDOM_TEXTURE_SUFFIX_2.containsKey(id)) {
                            UUID_RANDOM_TEXTURE_SUFFIX_2.put(id, 0);
                        }
                        UUID_ENTITY_ALREADY_CALCULATED.add(id);
                    }
                    // System.out.println("suffix was ="+UUID_randomTextureSuffix.get(id));
                    if (UUID_RANDOM_TEXTURE_SUFFIX_2.get(id) == 0) {
                        if (!PATH_HAS_DEFAULT_REPLACEMENT.containsKey(vanilla.toString())) {
                            PATH_HAS_DEFAULT_REPLACEMENT.put(vanilla.toString(), ETFUtils.isExistingNativeImageFile(ETFUtils.returnOptifineOrVanillaIdentifier(path)));
                        }
                        if (PATH_HAS_DEFAULT_REPLACEMENT.get(vanilla.toString())) {
                            return ETFUtils.returnBlinkIdOrGiven(entity, ETFUtils.returnOptifineOrVanillaIdentifier(path).toString(), id);
                        } else {
                            return ETFUtils.returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                        }

                    } else {
                        return ETFUtils.returnBlinkIdOrGiven(entity, ETFUtils.returnOptifineOrVanillaIdentifier(path, UUID_RANDOM_TEXTURE_SUFFIX_2.get(id)).toString(), id);
                    }

                } else {//true random assign
                    UUID_HAS_UPDATABLE_RANDOM_CASES_2.put(id, false);
                    if (PATH_TOTAL_TRUE_RANDOM.get(path) > 0) {
                        if (!UUID_RANDOM_TEXTURE_SUFFIX_2.containsKey(id)) {
                            int randomReliable = Math.abs(id.hashCode());
                            randomReliable %= PATH_TOTAL_TRUE_RANDOM.get(path);
                            randomReliable++;
                            if (randomReliable == 1 && PATH_IGNORE_ONE_PNG.get(path)) {
                                randomReliable = 0;
                            }
                            UUID_RANDOM_TEXTURE_SUFFIX_2.put(id, randomReliable);
                            UUID_ENTITY_ALREADY_CALCULATED.add(id);
                        }
                        if (UUID_RANDOM_TEXTURE_SUFFIX_2.get(id) == 0) {
                            return ETFUtils.returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                        } else {
                            return ETFUtils.returnBlinkIdOrGiven(entity, ETFUtils.returnOptifineOrVanillaPath(path, UUID_RANDOM_TEXTURE_SUFFIX_2.get(id), ""), id);
                        }
                    } else {
                        return ETFUtils.returnBlinkIdOrGiven(entity, vanilla.toString(), id);
                    }
                }
            } else {
                ETFUtils.modMessage("not random", false);
                return ETFUtils.returnBlinkIdOrGiven(entity, vanilla.toString(), id);
            }

        } catch (Exception e) {
            ETFUtils.modMessage(e.toString(), false);
            return ETFUtils.returnBlinkIdOrGiven(entity, vanilla.toString(), id);
        }
    }

}
