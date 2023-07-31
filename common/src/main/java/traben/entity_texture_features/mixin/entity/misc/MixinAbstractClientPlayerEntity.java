package traben.entity_texture_features.mixin.entity.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.texture_handlers.ETFPlayerTexture;

import static traben.entity_texture_features.ETFClientCommon.MOD_ID;


@Mixin(AbstractClientPlayerEntity.class)
public abstract class MixinAbstractClientPlayerEntity extends PlayerEntity {


    @SuppressWarnings("unused")
    public MixinAbstractClientPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow
    public abstract Identifier getSkinTexture();

    @Inject(method = "getCapeTexture",
            at = @At("RETURN"), cancellable = true)
    private void changeCapeReturnsToNotNull(CallbackInfoReturnable<Identifier> cir) {
        //requires a non-null return for elytras and for enabling cape rendering which is itself overridden by etf

        Identifier cape = cir.getReturnValue();
        if (cape != null) {
            //catches cit elytras makes them override ETF cape returns
            if (cape.toString().contains("/cit/")) {
                return;
            }
        }
        ETFPlayerTexture textureData = ETFManager.getInstance().getPlayerTexture(this, getSkinTexture());
        if (textureData != null && textureData.hasCustomCape()) {
            cir.setReturnValue(textureData.etfCapeIdentifier);
        }else if (getUuid().equals(ETFPlayerTexture.Dev)) {
            cir.setReturnValue(new Identifier(MOD_ID, "textures/capes/etf.png"));
        }else if (getUuid().equals(ETFPlayerTexture.Wife)) {
            cir.setReturnValue(new Identifier(MOD_ID, "textures/capes/wife.png"));
        }
    }

    @Inject(method = "canRenderCapeTexture",
            at = @At("RETURN"),
            cancellable = true)
    private void changeCapeReturnsBoolean(CallbackInfoReturnable<Boolean> cir) {
        //returns null if skin features disabled check is inbuilt
        ETFPlayerTexture textureData = ETFManager.getInstance().getPlayerTexture(this, getSkinTexture());
        if ((textureData != null && textureData.hasCustomCape())
                || getUuid().equals(ETFPlayerTexture.Dev)
                || getUuid().equals(ETFPlayerTexture.Wife)) {
            cir.setReturnValue(true);
        }
    }
}