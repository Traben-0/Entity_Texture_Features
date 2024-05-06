package traben.entity_texture_features.compat;

import me.jellysquid.mods.sodium.client.render.vertex.buffer.ExtendedBufferBuilder;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.apache.logging.log4j.util.TriConsumer;
import traben.entity_texture_features.features.ETFRenderContext;
import traben.entity_texture_features.utils.ETFVertexConsumer;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.Objects;

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
        try {
            return new Impl();
        } catch (NoClassDefFoundError | NullPointerException e) {
            return null;
        }
    }

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
}
