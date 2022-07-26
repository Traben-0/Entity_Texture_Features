package traben.entity_texture_features.mixin.entity.misc;

import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static traben.entity_texture_features.ETFClientCommon.ETFConfigData;

@Mixin(ElytraEntityModel.class)
public abstract class MixinElytraEntityModel<T extends LivingEntity> extends AnimalModel<T> {

    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 2.0F))
    private static float etf$injected(float size) {
        if (ETFConfigData.elytraThicknessFix) {
            return 0.5f;
        }
        return size;
    }
}


