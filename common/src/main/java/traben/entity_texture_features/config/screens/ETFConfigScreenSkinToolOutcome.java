package traben.entity_texture_features.config.screens;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import traben.entity_texture_features.ETFClientCommon;
import traben.entity_texture_features.ETFVersionDifferenceHandler;
import traben.entity_texture_features.mixin.accessor.PlayerSkinProviderAccessor;
import traben.entity_texture_features.mixin.accessor.PlayerSkinTextureAccessor;
import traben.entity_texture_features.texture_handlers.ETFManager;
import traben.entity_texture_features.utils.ETFUtils2;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static traben.entity_texture_features.ETFClientCommon.CONFIG_DIR;

//inspired by puzzles custom gui code
public class ETFConfigScreenSkinToolOutcome extends ETFConfigScreen {
    private final boolean didSucceed;
    private final NativeImage skin;

    protected ETFConfigScreenSkinToolOutcome(Screen parent, boolean success, NativeImage skin) {
        super(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.print_skin.result"), parent);
        didSucceed = success;
        this.skin = skin;
        //this.skin = new PlayerSkinTexture(skin);
    }

    //upload code sourced from by https://github.com/cobrasrock/Skin-Swapper/blob/1.18-fabric/src/main/java/net/cobrasrock/skinswapper/changeskin/SkinChange.java
    //I do not intend to allow uploading of just any skin file, only ETF skin feature changes to an already existing skin, so I will not encroach on the scope of the excellent skin swapper mod
    public static boolean uploadSkin(boolean skinType) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            if ("127.0.0.1".equals(InetAddress.getLocalHost().getHostAddress())) {
                return false;
            }

            String auth = MinecraftClient.getInstance().getSession().getAccessToken();

            //uploads skin
            HttpPost http = new HttpPost("https://api.minecraftservices.com/minecraft/profile/skins");


            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("variant", skinType ? "classic" : "slim", ContentType.TEXT_PLAIN);
            assert CONFIG_DIR != null;
            builder.addBinaryBody(
                    "file",
                    new FileInputStream(Path.of(CONFIG_DIR.getParent(), "\\ETF_player_skin_printout.png").toFile()),
                    ContentType.IMAGE_PNG,
                    "skin.png"
            );

            http.setEntity(builder.build());
            http.addHeader("Authorization", "Bearer " + auth);
            HttpResponse response = httpClient.execute(http);

            return response.getStatusLine().getStatusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void init() {
        super.init();


        this.addDrawableChild(getETFButton((int) (this.width * 0.55), (int) (this.height * 0.9), (int) (this.width * 0.2), 20,
                ScreenTexts.DONE,
                (button) -> Objects.requireNonNull(client).setScreen(parent)));
        if (didSucceed) {
            this.addDrawableChild(getETFButton((int) (this.width * 0.15), (int) (this.height * 0.6), (int) (this.width * 0.7), 20,
                    ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.print_skin.open"),
                    (button) -> {
                        try {
                            @SuppressWarnings("ConstantConditions")
                            Path outputDirectory = Path.of(CONFIG_DIR.getParent());
                            Util.getOperatingSystem().open(outputDirectory.toFile());
                        } catch (Exception ignored) {
                        }
                    }));
            this.addDrawableChild(getETFButton((int) (this.width * 0.15), (int) (this.height * 0.4), (int) (this.width * 0.7), 20,
                    ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.upload_skin"),
                    (button) -> {
                        boolean skinType = true;//true for steve false for alex
                        if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().getNetworkHandler() != null) {
                            PlayerListEntry playerListEntry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(MinecraftClient.getInstance().player.getUuid());
                            if (playerListEntry != null) {
                                String skinTypeData = MinecraftClient.getInstance().getSkinProvider().getTextures(playerListEntry.getProfile()).get(MinecraftProfileTexture.Type.SKIN).getMetadata("model");
                                if (skinTypeData != null) {
                                    skinType = !"slim".equals(skinTypeData);
                                }
                            }
                        }
                        boolean changeSuccess = uploadSkin(skinType);
                        button.setMessage(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.upload_skin_v2." +
                                (changeSuccess ? "success" : "fail")));
                        if (changeSuccess) {
                            //ETFUtils2.logMessage(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.upload_skin.success" ).getString(),true);
                            //change internally cached skin
                            PlayerSkinTexture skinfile = (PlayerSkinTexture) ((PlayerSkinProviderAccessor) MinecraftClient.getInstance().getSkinProvider()).getTextureManager().getOrDefault((MinecraftClient.getInstance().player).getSkinTexture(), null);
                            try {
                                //System.out.println("file was ="+((PlayerSkinTextureAccessor)skinfile).getCacheFile().toString());
                                skin.writeTo(((PlayerSkinTextureAccessor) skinfile).getCacheFile());
                            } catch (IOException e) {
                                ETFUtils2.logError(ETFVersionDifferenceHandler.getTextFromTranslation("config." + ETFClientCommon.MOD_ID + ".player_skin_editor.upload_skin.success_local_fail").getString(), true);
                                //System.out.println("failed to change internal skin");
                            }
                            //clear etf data of skin
                            if (MinecraftClient.getInstance().player != null) {
                                ETFManager.getInstance().PLAYER_TEXTURE_MAP.removeEntryOnly(MinecraftClient.getInstance().player.getUuid());
                                ETFManager.getInstance().ENTITY_BLINK_TIME.put(MinecraftClient.getInstance().player.getUuid(), 0L);
                            }
                        }
                        button.active = false;
                    }));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        String[] strings =
                ETFVersionDifferenceHandler.getTextFromTranslation(
                        "config." + ETFClientCommon.MOD_ID + ".player_skin_editor.print_skin.result." + (didSucceed ? "success" : "fail")
                ).getString().split("\n");
        List<Text> lines = new ArrayList<>();

        for (String str :
                strings) {
            lines.add(Text.of(str.strip()));
        }
        int i = 0;
        for (Text txt :
                lines) {
            context.drawCenteredTextWithShadow( textRenderer, txt.asOrderedText(), (int) (width * 0.5), (int) (height * 0.3) + i, 0xFFFFFF);
            i += txt.getString().isBlank() ? 5 : 10;
        }


    }

}
