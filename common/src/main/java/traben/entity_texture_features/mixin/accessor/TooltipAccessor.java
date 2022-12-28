package traben.entity_texture_features.mixin.accessor;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Tooltip.class)
public interface TooltipAccessor {
    @Accessor
    void setLines(List<OrderedText> lines);
}
