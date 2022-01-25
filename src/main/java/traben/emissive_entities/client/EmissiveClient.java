package traben.emissive_entities.client;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.data.Main;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class EmissiveClient implements ModInitializer {
    public static Map<UUID,Integer[]> randomData = new HashMap<UUID, Integer[]>() ;

    public static Map<String, Identifier> hasEmissive = new HashMap<String, Identifier>() ;
    @Override
    public void onInitialize() {

    }
}
