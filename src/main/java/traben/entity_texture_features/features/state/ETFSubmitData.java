package traben.entity_texture_features.features.state;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ETFSubmitData {

    public ETFEntityRenderState backupState = ETFState.state();

    public final Map<String, Object> data = new HashMap<>();




    //#if MC >= 1.21.9
    public static final List<BiConsumer<ETFSubmitData,
                //#if MC >= 26.2
                //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit
                //#else
                net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit
                //#endif
                >> DATA_IN = new ArrayList<>();

    public static final List<BiConsumer<ETFSubmitData,
            //#if MC >= 26.2
            //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit
            //#else
            net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit
            //#endif
            >> DATA_OUT = new ArrayList<>();

    @Nullable
    public static ETFSubmitData from(
            //#if MC >= 26.2
            //$$ net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit<?> modelSubmit
            //#else
            net.minecraft.client.renderer.SubmitNodeStorage.ModelSubmit<?> modelSubmit
            //#endif
    ) {
        //noinspection ConstantValue
        return ((Object) modelSubmit) instanceof ETFSubmitExtension emf
                ? ((ETFSubmitExtension) (Object) modelSubmit).emf$getData()
                : null;
    }
    //#endif

}
