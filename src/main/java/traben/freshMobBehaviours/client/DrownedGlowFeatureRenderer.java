//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package traben.freshMobBehaviours.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DrownedGlowFeatureRenderer<T extends ZombieEntity> extends EyesFeatureRenderer<T, ZombieEntityModel<T>> {
    private static final RenderLayer SKIN = RenderLayer.getEyes(new Identifier("fresh_mob_behaviours","textures/entity/zombie/drowned_glowing_layer.png"));

    public DrownedGlowFeatureRenderer(FeatureRendererContext<T, ZombieEntityModel<T>> featureRendererContext) {
        super(featureRendererContext);
    }

    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}
