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

import java.util.UUID;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(VillagerClothingFeatureRenderer.class)
public abstract class MIX_VillagerClothingFeatureRenderer<T extends LivingEntity & VillagerDataContainer, M extends EntityModel<T> & ModelWithHat> extends FeatureRenderer<T, M> implements ETF_METHODS {

    public MIX_VillagerClothingFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
            at = @At(value = "HEAD"))
    private void getEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        villager = livingEntity;
    }

    T villager = null;

    @Inject(method = "findTexture",
            at = @At(value = "RETURN"), cancellable = true)
    private void returnAlteredTexture(String keyType, Identifier keyId, CallbackInfoReturnable<Identifier> cir) {
        if (villager != null) {
            cir.setReturnValue(returnAltered(cir.getReturnValue()));
        }


    }

    private Identifier returnAltered(Identifier vanillaTexture) {
        UUID id = villager.getUuid();
        if (ETFConfigData.enableCustomTextures) {
            if (!Texture_OptifineOrTrueRandom.containsKey(vanillaTexture.toString())) {
                processNewRandomTextureCandidate(vanillaTexture.toString());
            }else if (optifineOldOrVanilla.containsKey(vanillaTexture.toString())) {
                if (UUID_randomTextureSuffix.containsKey(id)) {
                    if (UUID_randomTextureSuffix.get(id) != 0) {
                        Identifier randomTexture = returnOptifineOrVanillaIdentifier(vanillaTexture.toString(), UUID_randomTextureSuffix.get(id));
                        if (!TEXTURE_VillagerIsExistingFeature.containsKey(randomTexture.toString())) {
                            TEXTURE_VillagerIsExistingFeature.put(randomTexture.toString(), isExistingFile(randomTexture));
                        }
                        if (TEXTURE_VillagerIsExistingFeature.get(randomTexture.toString())) {
                            //can use random texture
                            return randomTexture;
                        }
                    }
                }
            }
        }
        return vanillaTexture;
    }
}
