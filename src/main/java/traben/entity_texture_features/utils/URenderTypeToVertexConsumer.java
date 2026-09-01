package traben.entity_texture_features.utils;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;

public class URenderTypeToVertexConsumer {

    //#if MC >= 26.2
    //$$ public final net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer featureRenderer;
    //$$
    //$$ public URenderTypeToVertexConsumer(net.minecraft.client.renderer.feature.RenderTypeFeatureRenderer featureRenderer) {
    //$$     this.featureRenderer = featureRenderer;
    //$$ }
    //$$
    //$$ public VertexConsumer getBuffer(RenderType type) {
    //$$     return featureRenderer.getVertexBuilder(type);
    //$$ }
    //$$
    //#else
    public final net.minecraft.client.renderer.MultiBufferSource delegate;

    public URenderTypeToVertexConsumer(net.minecraft.client.renderer.MultiBufferSource delegate) {
        this.delegate = delegate;
    }

    public VertexConsumer getBuffer(RenderType type) {
        return delegate.getBuffer(type);
    }
    //#endif

}
