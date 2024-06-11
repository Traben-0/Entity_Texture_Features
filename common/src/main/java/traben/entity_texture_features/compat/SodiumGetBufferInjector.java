package traben.entity_texture_features.compat;

#if MC == MC_20_1 || MC >= MC_20_4
import me.jellysquid.mods.sodium.client.render.vertex.buffer.ExtendedBufferBuilder;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFVertexConsumer;
import java.util.Objects;
#endif
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.apache.logging.log4j.util.TriConsumer;

import com.mojang.blaze3d.vertex.VertexConsumer;



/**
 * A separate class to handle a specific sodium injection case for sodium 0.5.6+
 * This is called in one place and handles usage of sodium classes in a quarantined way.
 */
public abstract class SodiumGetBufferInjector {

    private static final TriConsumer<MultiBufferSource, RenderType, VertexConsumer> INSTANCE = get();

    public static void inject(MultiBufferSource provider, RenderType renderLayer, VertexConsumer vertexConsumer) {
        if (INSTANCE != null) INSTANCE.accept(provider, renderLayer, vertexConsumer);
    }

    private static TriConsumer<MultiBufferSource, RenderType, VertexConsumer> get() {
        #if MC == MC_20_1 || MC >= MC_20_4
        try {
            return new Impl();
        } catch (NoClassDefFoundError | NullPointerException ignored) {}
        #endif
        return null;
    }
#if MC == MC_20_1 || MC >= MC_20_4
    private static class Impl implements TriConsumer<MultiBufferSource, RenderType, VertexConsumer> {
        Impl() {
            Objects.requireNonNull(ExtendedBufferBuilder.class);
        }

        @Override
        public void accept(final MultiBufferSource vertexConsumerProvider, final RenderType renderLayer, final VertexConsumer vertexConsumer) {
            if (vertexConsumer instanceof ExtendedBufferBuilder buff) {
                var delegate = buff.sodium$getDelegate();
                if (delegate instanceof ETFVertexConsumer) {
                    ETFRenderContext.insertETFDataIntoVertexConsumer(vertexConsumerProvider, renderLayer, delegate);
                }
            }
        }
    }
    #endif
}
