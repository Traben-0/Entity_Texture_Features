package traben.entity_texture_features.features.state;

import com.mojang.blaze3d.systems.RenderSystem;
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
    private static final int POOL_CAPACITY = 4096;
    private static final ETFSubmitData[] POOL = new ETFSubmitData[POOL_CAPACITY];
    private static int poolSize = 0;
    private static int poolNext = 0;

    public static ETFSubmitData acquire() {
        if (!RenderSystem.isOnRenderThread()) return new ETFSubmitData();
        if (poolNext < poolSize) {
            var data = POOL[poolNext++];
            data.backupState = ETFState.state();
            return data;
        }
        var data = new ETFSubmitData();
        if (poolSize < POOL_CAPACITY) {
            POOL[poolSize++] = data;
            poolNext = poolSize;
        }
        return data;
    }

    public static void recycle() {
        for (int i = 0; i < poolNext; i++) {
            POOL[i].backupState = null;
            POOL[i].data.clear();
        }
        poolNext = 0;
    }

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
