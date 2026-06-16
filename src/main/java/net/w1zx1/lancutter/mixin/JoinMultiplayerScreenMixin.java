package net.w1zx1.lancutter.mixin;

import net.w1zx1.lancutter.LancutterConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.server.LanServer;
import net.minecraft.client.server.LanServerDetection;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin extends Screen {

    @Shadow
    private LanServerDetection.LanServerList lanServerList;

    @Shadow
    private ServerSelectionList serverSelectionList;

    @Unique
    private EditBox limitField;

    @Unique
    private Button applyButton;

    @Unique
    private static final int buttonWidth = 60;

    @Unique
    private static final int fieldWidth = 70;

    @Unique
    private static final int widgetHeight = 20;

    @Unique
    private static final int widgetSpacing = 5;

    @Unique
    private static final int topMargin = 6;

    protected JoinMultiplayerScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addLanFilterControls(CallbackInfo ci) {
        Font font = this.font;

        int fieldX = this.width - fieldWidth - buttonWidth - widgetSpacing - 10;
        int fieldY = topMargin;

        limitField = new EditBox(font, fieldX, fieldY, fieldWidth, widgetHeight,
                Component.literal(String.valueOf(LancutterConfig.maxLanServers)));
        limitField.setMaxLength(4);
        limitField.setValue(String.valueOf(LancutterConfig.maxLanServers));
        limitField.setResponder(text -> {
            if (!text.isEmpty() && !text.matches("\\d*")) {
                limitField.setValue(text.replaceAll("[^\\d]", ""));
            }
        });

        int buttonX = this.width - buttonWidth - 10;
        applyButton = Button.builder(
                Component.literal("Apply"),
                button -> {
                    String text = limitField.getValue();
                    if (!text.isEmpty()) {
                        try {
                            int parsed = Integer.parseInt(text);
                            LancutterConfig.setMaxLanServers(parsed);
                            limitField.setValue(String.valueOf(LancutterConfig.maxLanServers));
                            refreshLanDisplay();
                        } catch (NumberFormatException e) {
                            limitField.setValue("9999");
                        }
                    }
                }
        ).bounds(buttonX, fieldY, buttonWidth, widgetHeight).build();

        this.addRenderableWidget(limitField);
        this.addRenderableWidget(applyButton);
    }

    @Inject(method = "repositionElements", at = @At("TAIL"))
    private void onRepositionElements(CallbackInfo ci) {
        if (limitField == null || applyButton == null) return;

        int fieldX = this.width - fieldWidth - buttonWidth - widgetSpacing - 10;
        int buttonX = this.width - buttonWidth - 10;

        limitField.setX(fieldX);
        limitField.setY(topMargin);
        applyButton.setX(buttonX);
        applyButton.setY(topMargin);
    }

    @Unique
    private void refreshLanDisplay() {
        List<LanServer> allServers = ((LanServerListAccessor) this.lanServerList).getServers();
        int limit = LancutterConfig.maxLanServers;
        List<LanServer> filtered = allServers.size() > limit
                ? List.copyOf(allServers.subList(0, limit))
                : allServers;
        this.serverSelectionList.updateNetworkServers(filtered);
    }
}
