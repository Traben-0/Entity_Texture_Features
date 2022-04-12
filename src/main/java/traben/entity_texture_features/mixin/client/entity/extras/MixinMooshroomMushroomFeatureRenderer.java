package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.client.ETFUtils;

import static traben.entity_texture_features.client.ETF_CLIENT.ETFConfigData;
import static traben.entity_texture_features.client.ETFClient.*;

@Mixin(MooshroomMushroomFeatureRenderer.class)
public abstract class MixinMooshroomMushroomFeatureRenderer {

    @ModifyArg(method = "renderMushroom", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getOutline(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"), index = 0)
    private Identifier etf$injected(Identifier texture) {
        //enable custom mooshroom mushrooms
        if (ETFConfigData.enableCustomTextures) {
            if (texture.toString().equals("minecraft:textures/block/red_mushroom.png")) {
                switch (mooshroomRedCustomShroom) {
                    case 1:
                        return texture;
                    case 2:
                        return new Identifier("minecraft:textures/entity/cow/red_mushroom.png");
                    default: {
                        Identifier test = new Identifier("minecraft:textures/entity/cow/red_mushroom.png");
                        if (ETFUtils.isExistingNativeImageFile(test)) {
                            mooshroomRedCustomShroom = 2;
                            return test;
                        } else {
                            mooshroomRedCustomShroom = 1;
                        }
                    }
                }
            }
            if (texture.toString().equals("minecraft:textures/block/brown_mushroom.png")) {
                switch (mooshroomBrownCustomShroom) {
                    case 1:
                        return texture;
                    case 2:
                        return new Identifier("minecraft:textures/entity/cow/brown_mushroom.png");
                    default: {
                        Identifier test = new Identifier("minecraft:textures/entity/cow/brown_mushroom.png");
                        if (ETFUtils.isExistingNativeImageFile(test)) {
                            mooshroomBrownCustomShroom = 2;
                            return test;
                        } else {
                            mooshroomBrownCustomShroom = 1;
                        }
                    }
                }
            }
        }
        return texture;
    }
}