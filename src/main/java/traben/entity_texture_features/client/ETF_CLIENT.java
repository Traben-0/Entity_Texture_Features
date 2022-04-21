package traben.entity_texture_features.client;

import traben.entity_texture_features.config.ETFConfig;

//this class must be preserved for puzzle mod support until full release
//this just redirects to @see ETFClient
public class ETF_CLIENT {
    //this needs to be here instead of @see ETFClient due to puzzle mod compatibility, move this when the full release happens
    //this is the Config / settings object used to customize ETF
    public static ETFConfig ETFConfigData;
}
