package traben.emissive_entities.mixin.client.entity.multitexture;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.AxolotlEntityRenderer;
import net.minecraft.client.render.entity.DonkeyEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.emissive_entities.client.EmissiveFeatureRenderer;

import static traben.emissive_entities.client.EmissiveClient.addEmissiveRenderer;

@Mixin(DonkeyEntityRenderer.class)
public class MIX_DonkeyEntityRenderer<T extends AbstractDonkeyEntity> {
    public DonkeyEntity mainEntity;

    @Inject(method = "getTexture(Lnet/minecraft/entity/passive/AbstractDonkeyEntity;)Lnet/minecraft/util/Identifier;", at = @At("HEAD"))
    private void injected(T abstractDonkeyEntity, CallbackInfoReturnable<Identifier> cir) {
        mainEntity = (DonkeyEntity) abstractDonkeyEntity;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injected(EntityRendererFactory.Context ctx, float scale, EntityModelLayer layer, CallbackInfo ci) {
        LivingEntityRenderer renderer = (LivingEntityRenderer) (Object)this;
        FeatureRenderer[]adding= {new EmissiveFeatureRenderer(renderer){
            @Override
            public RenderLayer getEyesTexture() {
                String path = getTexture(mainEntity).getPath().replace(".png","_e.png");
                return RenderLayer.getEyes(new Identifier(path));
            }
        }};
        addEmissiveRenderer(renderer,ctx,adding);
    }
}
