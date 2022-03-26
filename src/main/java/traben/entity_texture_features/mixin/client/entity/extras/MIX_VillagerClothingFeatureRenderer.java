package traben.entity_texture_features.mixin.client.entity.extras;

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
import traben.entity_texture_features.client.ETF_METHODS;

import java.util.HashMap;
import java.util.UUID;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(VillagerClothingFeatureRenderer.class)
public abstract class MIX_VillagerClothingFeatureRenderer<T extends LivingEntity & VillagerDataContainer, M extends EntityModel<T> & ModelWithHat> extends FeatureRenderer<T, M> implements ETF_METHODS {

    public MIX_VillagerClothingFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render*",
            at = @At(value = "HEAD"))
    private void getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        villager = livingEntity;
    }

    T villager = null;

    @Inject(method = "findTexture",
            at = @At(value = "RETURN"), cancellable = true)
    private void returnAlteredTexture(String keyType, Identifier keyId, CallbackInfoReturnable<Identifier> cir) {
        if (villager != null) {
            cir.setReturnValue(
                    switch (keyType){
                        //base villager uses  suffix1
                case "type" -> returnAltered(cir.getReturnValue(),UUID_randomTextureSuffix2,hasUpdatableRandomCases2);
                case "profession" -> returnAltered(cir.getReturnValue(),UUID_randomTextureSuffix3,hasUpdatableRandomCases3);
                case "profession_level" -> returnAltered(cir.getReturnValue(),UUID_randomTextureSuffix4,hasUpdatableRandomCases4);
                default -> cir.getReturnValue();
            });


        }


    }


    private Identifier returnAltered(Identifier vanillaTexture, HashMap<UUID,Integer> UUID_RandomSuffixMap, HashMap<UUID,Boolean> UUID_HasUpdateables) {
        UUID id = villager.getUuid();
        if (ETFConfigData.enableCustomTextures) {
            if (!Texture_OptifineOrTrueRandom.containsKey(vanillaTexture.toString())) {
                ETF_processNewRandomTextureCandidate(vanillaTexture.toString());
            }else if (PATH_OptifineOldVanillaETF_0123.containsKey(vanillaTexture.toString())) {
                if (Texture_OptifineOrTrueRandom.get(vanillaTexture.toString())) {
                    if (!UUID_RandomSuffixMap.containsKey(id)) {
                        ETF_testCases(vanillaTexture.toString(), id, villager, false, UUID_RandomSuffixMap, UUID_HasUpdateables);
                        //if all failed set to vanilla
                        if (!UUID_RandomSuffixMap.containsKey(id)) {
                            UUID_RandomSuffixMap.put(id, 0);
                        }
                        //UUID_entityAlreadyCalculated.add(id);
                    }
                    if (UUID_RandomSuffixMap.containsKey(id)) {
                        if (UUID_RandomSuffixMap.get(id) != 0) {
                            Identifier randomTexture = ETF_returnOptifineOrVanillaIdentifier(vanillaTexture.toString(), UUID_RandomSuffixMap.get(id));
                            if (!TEXTURE_VillagerIsExistingFeature.containsKey(randomTexture.toString())) {
                                TEXTURE_VillagerIsExistingFeature.put(randomTexture.toString(), ETF_isExistingFile(randomTexture));
                            }
                            if (TEXTURE_VillagerIsExistingFeature.get(randomTexture.toString())) {
                                //can use random texture
                                return randomTexture;
                            }
                        }
                    }
                }else{
                    UUID_HasUpdateables.put(id, false);
                    if (Texture_TotalTrueRandom.get(vanillaTexture.toString()) > 0) {
                        if (!UUID_RandomSuffixMap.containsKey(id)) {
                            int randomReliable = Math.abs(id.hashCode());
                            randomReliable %= Texture_TotalTrueRandom.get(vanillaTexture.toString());
                            randomReliable++;
                            if (randomReliable == 1 && ignoreOnePNG.get(vanillaTexture.toString())) {
                                randomReliable = 0;
                            }
                            UUID_RandomSuffixMap.put(id, randomReliable);
                            //UUID_entityAlreadyCalculated.add(id);
                        }
                        if (UUID_RandomSuffixMap.get(id) == 0) {
                            return ETF_returnBlinkIdOrGiven(villager, vanillaTexture.toString(), id);
                        } else {
                            return ETF_returnBlinkIdOrGiven(villager, ETF_returnOptifineOrVanillaPath(vanillaTexture.toString(), UUID_RandomSuffixMap.get(id), ""), id);
                        }
                    } else {
                        return ETF_returnBlinkIdOrGiven(villager, vanillaTexture.toString(), id);
                    }
                }
            }
        }
        return vanillaTexture;
    }
}
