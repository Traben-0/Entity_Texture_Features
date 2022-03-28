package traben.entity_texture_features.mixin.client.entity.extras;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static traben.entity_texture_features.client.ETF_CLIENT.ETF_ConfigData;

@Mixin(ElytraEntityModel.class)
public abstract class MIX_ElytraEntityModel<T extends LivingEntity> extends AnimalModel<T> {

    @ModifyArg(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPartBuilder;cuboid(FFFFFFLnet/minecraft/client/model/Dilation;)Lnet/minecraft/client/model/ModelPartBuilder;"), index = 6)
    private static Dilation ETF_injected(Dilation dilation) {
        if (ETF_ConfigData.elytraThicknessFix) {
            return new Dilation(1, 1, 0.2f);
        }
        return dilation;
    }
}


