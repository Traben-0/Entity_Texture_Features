package traben.entity_features.config;

import com.google.gson.GsonBuilder;
import net.minecraft.util.Identifier;
import traben.entity_features.config.gui.builders.EFOptionCategory;

public abstract class EFConfig {

    public abstract EFOptionCategory getGUIOptions();

    public abstract Identifier getModIcon();

    public String toJson(){
        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }

    @Override
    public boolean equals(final Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final EFConfig that = (EFConfig) obj;
        return toJson().equals(that.toJson());
    }

    @Override
    public int hashCode(){return toJson().hashCode();}


    public static class NoGUI extends EFConfig{
        @Override
        public EFOptionCategory getGUIOptions() {
            return null;
        }

        @Override
        public Identifier getModIcon() {
            return null;
        }
    }

}
