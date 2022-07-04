package traben.entity_texture_features.mixin.accessor;

import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Sprite.class)
public interface SpriteAccessor {
    @Invoker
    int callGetFrameCount();
}
