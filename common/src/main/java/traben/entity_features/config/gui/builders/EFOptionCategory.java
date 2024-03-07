package traben.entity_features.config.gui.builders;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import traben.entity_features.config.gui.EFOptionsScreen;

public class EFOptionCategory extends EFOption {

    public Object2ObjectLinkedOpenHashMap<String, EFOption> getOptions() {
        return options;
    }

    private final Object2ObjectLinkedOpenHashMap<String,EFOption> options = new Object2ObjectLinkedOpenHashMap<>();

    private EFOptionsScreen screen = null;

    //don't need to init screen each time
    private EFOptionsScreen getScreen(){
        if(screen == null){
            screen = new EFOptionsScreen(translationKey,MinecraftClient.getInstance().currentScreen, options.values().toArray(new EFOption[0]),this::setValuesToDefault,this::resetValuesToInitial);
        }
        return screen;
    }

    @Override
    public <T extends Element & Drawable & Selectable> T  getWidget(final int x, final int y, final int width, final int height) {
        //noinspection unchecked
        return (T) ButtonWidget.builder(getText(),(button)-> MinecraftClient.getInstance().setScreen(getScreen())
        ).dimensions(x,y,width,height).tooltip(getTooltip()).build();
    }

    @Override
    public boolean saveValuesToConfig() {
        boolean found = false;
        for (EFOption option : options.values()) {
            found |= option.saveValuesToConfig();
        }
        return found;
    }

    @Override
    public void setValuesToDefault() {
        for (EFOption option : options.values()) {
            option.setValuesToDefault();
        }
    }

    @Override
    public void resetValuesToInitial() {
        for (EFOption option : options.values()) {
            option.resetValuesToInitial();
        }
    }

    private final String translationKey;
    public EFOptionCategory(final String text, final String tooltip) {
        super(text, tooltip);
        translationKey = text;
    }




    public EFOptionCategory add(final EFOption... option) {
        for (EFOption efOption : option) {
            add(efOption);
        }
        return this;
    }
    public EFOptionCategory add(final EFOption option){
        if (option instanceof EFOptionCategory category){
            return addOrMerge(category);
        }
        options.put(option.getText().getString(),option);
        return this;
    }

    private EFOptionCategory addOrMerge(final EFOptionCategory category){
        if(options.containsKey(category.getText().getString())
        && options.get(category.getText().getString()) instanceof EFOptionCategory existingCategory){
            for (EFOption option : category.options.values()) {
                existingCategory.add(option);
            }
        }else{
            options.put(category.getText().getString(),category);
        }
        return this;
    }

    public static class Empty extends EFOptionCategory{
        public Empty() {
            super("", null);
        }

        @Override
        public <T extends Element & Drawable & Selectable> T getWidget(int x, int y, int width, int height) {
            return null;
        }


    }

}
