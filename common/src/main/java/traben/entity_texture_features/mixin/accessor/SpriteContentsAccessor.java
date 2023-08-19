package traben.entity_texture_features.mixin.accessor;

import net.minecraft.client.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpriteContents.class)
public interface SpriteContentsAccessor {
    @Invoker
    int callGetFrameCount();

    @Accessor
    int getWidth();

    @Mutable
    @Accessor
    void setWidth(int width);

    @Accessor
    int getHeight();

    @Mutable
    @Accessor
    void setHeight(int height);


}
