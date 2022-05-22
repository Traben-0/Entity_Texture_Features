package traben.entity_texture_features.mixin.client.entity.featureRenderers;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerDataContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.client.utils.ETFUtils;

import java.util.HashMap;
import java.util.UUID;

import static traben.entity_texture_features.client.ETFClient.*;

@Mixin(VillagerClothingFeatureRenderer.class)
public abstract class MixinVillagerClothingFeatureRenderer<T extends LivingEntity & VillagerDataContainer, M extends EntityModel<T> & ModelWithHat> extends FeatureRenderer<T, M> {

    public MixinVillagerClothingFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void etf$getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        etf$villager = livingEntity;
    }

    T etf$villager = null;

    @Inject(method = "findTexture",
            at = @At(value = "RETURN"), cancellable = true)
    private void etf$returnAlteredTexture(String keyType, Identifier keyId, CallbackInfoReturnable<Identifier> cir) {
        if (etf$villager != null) {
            cir.setReturnValue(
                    switch (keyType) {
                        //base villager uses  suffix1
                        case "type" -> etf$returnAltered(cir.getReturnValue(), UUID_RANDOM_TEXTURE_SUFFIX_2, UUID_HAS_UPDATABLE_RANDOM_CASES_2);
                        case "profession" -> etf$returnAltered(cir.getReturnValue(), UUID_RANDOM_TEXTURE_SUFFIX_3, UUID_HAS_UPDATABLE_RANDOM_CASES_3);
                        case "profession_level" -> etf$returnAltered(cir.getReturnValue(), UUID_RANDOM_TEXTURE_SUFFIX_4, UUID_HAS_UPDATABLE_RANDOM_CASES_4);
                        default -> cir.getReturnValue();
                    });


        }


    }


    private Identifier etf$returnAltered(Identifier vanillaTexture, HashMap<UUID, Integer> UUID_RandomSuffixMap, HashMap<UUID, Boolean> UUID_HasUpdateables) {
        UUID id = etf$villager.getUuid();
        if (ETFConfigData.enableCustomTextures) {
            if (!PATH_OPTIFINE_OR_JUST_RANDOM.containsKey(vanillaTexture.toString())) {
                ETFUtils.processNewRandomTextureCandidate(vanillaTexture.toString());
            } else if (PATH_USES_OPTIFINE_OLD_VANILLA_ETF_0123.containsKey(vanillaTexture.toString())) {
                if (PATH_OPTIFINE_OR_JUST_RANDOM.get(vanillaTexture.toString())) {
                    if (!UUID_RandomSuffixMap.containsKey(id)) {
                        ETFUtils.testCases(vanillaTexture.toString(), id, etf$villager, false, UUID_RandomSuffixMap, UUID_HasUpdateables);
                        //if all failed set to vanilla
                        if (!UUID_RandomSuffixMap.containsKey(id)) {
                            UUID_RandomSuffixMap.put(id, 0);
                        }
                        //UUID_entityAlreadyCalculated.add(id);
                    }
                    if (UUID_RandomSuffixMap.containsKey(id)) {
                        if (UUID_RandomSuffixMap.get(id) != 0) {
                            Identifier randomTexture = ETFUtils.returnOptifineOrVanillaIdentifier(vanillaTexture.toString(), UUID_RandomSuffixMap.get(id));
                            if (!PATH_IS_EXISTING_FEATURE.containsKey(randomTexture.toString())) {
                                PATH_IS_EXISTING_FEATURE.put(randomTexture.toString(), ETFUtils.isExistingNativeImageFile(randomTexture));
                            }
                            if (PATH_IS_EXISTING_FEATURE.get(randomTexture.toString())) {
                                //can use random texture
                                return randomTexture;
                            }
                        }
                    }
                } else {
                    UUID_HasUpdateables.put(id, false);
                    if (PATH_TOTAL_TRUE_RANDOM.get(vanillaTexture.toString()) > 0) {
                        if (!UUID_RandomSuffixMap.containsKey(id)) {
                            int randomReliable = Math.abs(id.hashCode());
                            randomReliable %= PATH_TOTAL_TRUE_RANDOM.get(vanillaTexture.toString());
                            randomReliable++;
                            if (randomReliable == 1 && PATH_IGNORE_ONE_PNG.get(vanillaTexture.toString())) {
                                randomReliable = 0;
                            }
                            UUID_RandomSuffixMap.put(id, randomReliable);
                            //UUID_entityAlreadyCalculated.add(id);
                        }
                        if (UUID_RandomSuffixMap.get(id) == 0) {
                            return ETFUtils.returnBlinkIdOrGiven(etf$villager, vanillaTexture.toString(), id);
                        } else {
                            return ETFUtils.returnBlinkIdOrGiven(etf$villager, ETFUtils.returnOptifineOrVanillaPath(vanillaTexture.toString(), UUID_RandomSuffixMap.get(id), ""), id);
                        }
                    } else {
                        return ETFUtils.returnBlinkIdOrGiven(etf$villager, vanillaTexture.toString(), id);
                    }
                }
            }
        }
        return vanillaTexture;
    }
}
