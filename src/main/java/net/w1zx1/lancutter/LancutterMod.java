package net.w1zx1.lancutter;

import net.fabricmc.api.ClientModInitializer;

public class LancutterMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LancutterConfig.load();
    }
}
