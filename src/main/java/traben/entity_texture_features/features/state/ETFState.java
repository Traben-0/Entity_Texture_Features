package traben.entity_texture_features.features.state;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import traben.entity_texture_features.ETF;
import traben.entity_texture_features.config.ETFConfig;
import traben.entity_texture_features.features.ETFManager;
import traben.entity_texture_features.utils.ETFRenderLayerWithTexture;
import traben.entity_texture_features.utils.ETFUtils2;
import traben.entity_texture_features.utils.ETFVertexConsumer;
import traben.entity_texture_features.utils.URenderTypeToVertexConsumer;

import java.util.Deque;
import java.util.EmptyStackException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Global state data for ETF
 * <p>
 * Also allows global access to the currently mounted ETFEntityRenderState in the stack
 */
public abstract class ETFState {

    private static final Deque<ETFEntityRenderState> stateStack = new ConcurrentLinkedDeque<>(){
        @Override
        public void push(ETFEntityRenderState etfEntityRenderState) {
            super.push(etfEntityRenderState == null ? ETFEntityRenderState.NULL : etfEntityRenderState);
        }

        @Override
        public ETFEntityRenderState peek() {
            var peeked = super.peek();
            if (peeked == ETFEntityRenderState.NULL) return null;
            return peeked;
        }
    };


    public static void mount(@Nullable ETFEntityRenderState state) {
        if (state != null && state() == state) return;

        var current = state();
        if (current != null) current.deactivate(true);
        stateStack.push(state);
        if (state != null) state.activate(true);
        pushRenderLayerModifyState(true);
        announceStack("mount");
    }

    public static void unMount() {
        try {
            var popped = stateStack.peek();
            if (popped != null) popped.deactivate(false);
            stateStack.pop();
            var current = state();
            if (current != null) current.activate(false);
        } catch (NoSuchElementException e) {
            // You'd think the isEmpty() check would avoid this
        }
        currentModelPartDepth = 0;
        popRenderLayerModifyState();
        announceStack("unmount");
    }

    public static void mountNone() {
        mount(null);
    }


    public static @Nullable ETFEntityRenderState state() {
        return stateStack.peek();
    }

    public static boolean isStateActive() {
        return state() != null;
    }

    public static void clear() {
        var state = state();
        if (state != null) state.deactivate(false);
        stateStack.clear();
        specialPhaseStack.clear();
        renderLayerModifyStack.clear();

        currentModelPartDepth = 0;
        onlyRandomizeViaPropertiesFiles = false;
        isRenderingFeatures = false;
        allowTexturePatching = false;

//        ETFSubmitData.endInstance();
        announceStack("clear");
        stackVerifyHasFailed = false;
    }

    private static boolean stackVerifyHasFailed = false;

    public static void announceStack(String action) {
        if (!stackVerifyHasFailed || !ETF.config().getConfig().stackDebugPrinting) return;
        var str = new StringBuilder();
        str.append("ETF state stack: \n - action = ").append(action)
                .append("\n - size = ").append(stateStack.size()).append("\n");
        int count = 1;
        for (var state : stateStack) {
            str.append(" ").append("-".repeat(count++)).append(" ").append(state != null ? state.entityKey() : "null").append("\n");
        }
        ETFUtils2.logWarn(str.toString());
        new Exception().printStackTrace();
    }

    public static void stackVerify(ETFEntityRenderState shouldBeHere) {
        boolean log = ETF.config().getConfig().stackDebugPrinting;
        if (shouldBeHere == null) {
            if (log) ETFUtils2.logWarn("ETFState stack verify called with null, this is likely a bug");
            stackVerifyHasFailed = true;
            return;
        }

        if (state() != shouldBeHere) {
            stackVerifyHasFailed = false;
            if (log) ETFUtils2.logWarn("ETFState stack is not correct");
            if (stateStack.contains(shouldBeHere)) {
                if (log) ETFUtils2.logWarn("ETFState stack is too deep");
                while (!stateStack.isEmpty() && stateStack.peek() != shouldBeHere) {
                    if (log) ETFUtils2.logWarn("- unmounting " + stateStack.peek().entityKey() );
                    unMount();
                }
            } else {
                // Error level failure, the above is somewhat expected if a crash or other mod mixin cancellation occurs
                // but this is not, this also likely doesn't really fix it
                ETFUtils2.logError("ETFState stack has popped early");
                mount(shouldBeHere);
            }
            stackVerifyHasFailed = true;
        }
    }

    public static void stackVerifyEmpty() {
        if (!stateStack.isEmpty()) {
            stackVerifyHasFailed = false;
            boolean log = ETF.config().getConfig().stackDebugPrinting;
            if (log) ETFUtils2.logWarn("ETFState stack is not empty on verifyEmpty, clearing stack");
            while (!stateStack.isEmpty()) {
                if (log) ETFUtils2.logWarn("- unmounting " + stateStack.peek().entityKey() );
                unMount();
            }
            stackVerifyHasFailed = true;
        }
        specialPhaseStack.clear();
        renderLayerModifyStack.clear();
    }


    public static int currentModelPartDepth = 0;

    public static boolean isRenderingFeatures = false;
    public static boolean onlyRandomizeViaPropertiesFiles = false;
    public static boolean allowTexturePatching = false;

    // Stack here probably isn't required, merely a safeguard against early exiting of the phase
    private static final Deque<Boolean> specialPhaseStack = new ConcurrentLinkedDeque<>();
    //region specialPhaseStack methods
    @SuppressWarnings("unused") // EMF uses it
    public static boolean isIsInSpecialRenderOverlayPhase() {return !specialPhaseStack.isEmpty();}
    public static void startSpecialRenderOverlayPhase() {
        specialPhaseStack.push(true);
    }
    public static void endSpecialRenderOverlayPhase() {
        try {
            if (!specialPhaseStack.isEmpty()) specialPhaseStack.pop();
        } catch (NoSuchElementException e) {
            // You'd think the isEmpty() check would avoid this
        }
    }
    //endregion

    private static final Deque<Boolean> renderLayerModifyStack = new ConcurrentLinkedDeque<>();
    //region allowRenderLayerTextureModify methods
    public static boolean isAllowedToRenderLayerTextureModify() {
        if (!ETF.config().getConfig().canDoCustomTextures()) return false;
        Boolean peek = renderLayerModifyStack.peek();
        return peek == null || peek;
    }
    public static void pushRenderLayerModifyState(boolean allow) {
        renderLayerModifyStack.push(allow);
    }
    public static void popRenderLayerModifyState() {
        try {
            if (!renderLayerModifyStack.isEmpty()) renderLayerModifyStack.pop();
        } catch (NoSuchElementException e) {
            // You'd think the isEmpty() check would avoid this
        }
    }
    //endregion

    public static RenderType modifyRenderLayerIfRequired(RenderType value) {

        if (isStateActive()
                && isAllowedToRenderLayerTextureModify()) {
            var layer = ETF.config().getConfig().getRenderLayerOverride();
            if (layer != null
                    && !value.isOutline()
                    && value instanceof ETFRenderLayerWithTexture multiphase) {

                Optional<ResourceLocation> texture = multiphase.etf$getId();
                if (texture.isPresent()) {
                    pushRenderLayerModifyState(false);

                    RenderType forReturn = switch (layer) {
                        case TRANSLUCENT ->
                            //#if MC>= 12111
                            //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                            //#else
                                RenderType
                                        //#endif
                                        .entityTranslucent(texture.get());
                        case TRANSLUCENT_CULL ->
                            //#if MC >= 12103

                            //#if MC>= 12111
                            //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                            //#else
                                RenderType
                                        //#endif
                                        .entityTranslucent(texture.get());
                        //#else
                        //$$     RenderType.entityTranslucentCull(texture.get());
                        //#endif
                        case END ->
                            //#if MC>= 12111
                            //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                            //#else
                                RenderType
                                        //#endif
                                        .endGateway();
                        case OUTLINE ->
                            //#if MC>= 12111
                            //$$ net.minecraft.client.renderer.rendertype.RenderTypes
                            //#else
                                RenderType
                                        //#endif
                                        .outline(texture.get());
                    };
                    popRenderLayerModifyState();
                    return forReturn;

                }
            }
        }
        return value;
    }

    public static void insertETFDataIntoVertexConsumer(URenderTypeToVertexConsumer provider, RenderType renderLayer, VertexConsumer vertexConsumer) {
        if (isStateActive() && vertexConsumer instanceof ETFVertexConsumer etfVertexConsumer) {
            // need to store etf texture of consumer and original render layer
            // store provider as well for future actions
            etfVertexConsumer.etf$initETFVertexConsumer(provider, renderLayer);
        }
    }

    public static boolean canRenderInBrightMode() {
        boolean setForBrightMode = ETFManager.getEmissiveMode() == ETFConfig.EmissiveRenderModes.BRIGHT;
        if (setForBrightMode) {
            var currentEntity = state();
            if (currentEntity != null) {
                return currentEntity.canRenderBright();
            } else {
                // establish default rule
                return true;
            }
        }
        return false;
    }

    //#if MC < 12103
    //$$ public static boolean shouldEmissiveUseCullingLayer() {
    //$$     var currentEntity = state();
    //$$     if (currentEntity != null) {
    //$$         return currentEntity.isBlockEntity();
    //$$     } else {
    //$$         // establish default rule
    //$$         return true;
    //$$     }
    //$$ }
    //#endif
}
