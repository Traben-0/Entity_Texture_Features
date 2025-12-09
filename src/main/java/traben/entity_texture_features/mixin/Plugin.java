package traben.entity_texture_features.mixin;


import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Plugin implements IMixinConfigPlugin {

    private static final Logger log = LoggerFactory.getLogger(Plugin.class);

    @Override
    public void onLoad(final String mixinPackage) {
        MixinExtrasBootstrap.init(); // Initialize Mixin Extras if it isn't already initialized
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(final String targetClassName, final String mixinClassName) {
        if (mixinClassName.endsWith("MixinModelPartSodium")) {
            return hasClass("me.jellysquid.mods.sodium.client.render.immediate.model.EntityRenderer");
        }

        return !targetClassName.equals("traben.entity_texture_features.mixin.CancelTarget");
    }

    private boolean hasClass(final String className) {
        try {
            MixinService.getService().getBytecodeProvider().getClassNode(className);
            return true;
        } catch (ClassNotFoundException | IOException e) {
            return false;
        }
    }

    @Override
    public void acceptTargets(final Set<String> myTargets, final Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(final String targetClassName, final ClassNode targetClass, final String mixinClassName, final IMixinInfo mixinInfo) {

    }
}
