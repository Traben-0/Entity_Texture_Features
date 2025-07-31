package traben.entity_texture_features.features.state;

import traben.entity_texture_features.utils.ETFEntity;

public interface HoldsETFRenderState {
    ETFEntityRenderState etf$getState();
    void etf$initState(ETFEntity entity);
}