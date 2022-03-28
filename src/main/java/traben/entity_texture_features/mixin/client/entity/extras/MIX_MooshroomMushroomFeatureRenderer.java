package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import traben.entity_texture_features.client.ETF_METHODS;

import static traben.entity_texture_features.client.ETF_CLIENT.*;

@Mixin(MooshroomMushroomFeatureRenderer.class)
public abstract class MIX_MooshroomMushroomFeatureRenderer implements ETF_METHODS {

    @ModifyArg(method = "renderMushroom", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getOutline(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"), index = 0)
    private Identifier ETF_injected(Identifier texture) {
        //enable custom mooshroom mushrooms
        if (ETF_ConfigData.enableCustomTextures) {
            if (texture.toString().equals("minecraft:textures/block/red_mushroom.png")) {
                switch (ETF_mooshroomRedCustomShroom) {
                    case 1:
                        return texture;
                    case 2:
                        return new Identifier("minecraft:textures/entity/cow/red_mushroom.png");
                    default: {
                        Identifier test = new Identifier("minecraft:textures/entity/cow/red_mushroom.png");
                        if (ETF_isExistingFile(test)) {
                            ETF_mooshroomRedCustomShroom = 2;
                            return test;
                        } else {
                            ETF_mooshroomRedCustomShroom = 1;
                        }
                    }
                }
            }
            if (texture.toString().equals("minecraft:textures/block/brown_mushroom.png")) {
                switch (ETF_mooshroomBrownCustomShroom) {
                    case 1:
                        return texture;
                    case 2:
                        return new Identifier("minecraft:textures/entity/cow/brown_mushroom.png");
                    default: {
                        Identifier test = new Identifier("minecraft:textures/entity/cow/brown_mushroom.png");
                        if (ETF_isExistingFile(test)) {
                            ETF_mooshroomBrownCustomShroom = 2;
                            return test;
                        } else {
                            ETF_mooshroomBrownCustomShroom = 1;
                        }
                    }
                }
            }
        }
        return texture;
    }
}