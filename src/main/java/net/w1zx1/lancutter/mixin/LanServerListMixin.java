package net.w1zx1.lancutter.mixin;

import net.w1zx1.lancutter.LancutterConfig;
import net.minecraft.client.server.LanServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(targets = "net.minecraft.client.server.LanServerDetection$LanServerList")
public abstract class LanServerListMixin {

    @Shadow
    @Final
    private List<LanServer> servers;

    @Shadow
    private boolean isDirty;

    @Inject(method = "takeDirtyServers", at = @At("RETURN"), cancellable = true)
    private void limitTakeDirtyServers(CallbackInfoReturnable<List<LanServer>> cir) {
        List<LanServer> result = cir.getReturnValue();
        if (result == null) return;

        int limit = LancutterConfig.maxLanServers;
        if (result.size() > limit) {
            cir.setReturnValue(List.copyOf(result.subList(0, limit)));
        }
    }

    @Inject(method = "addServer", at = @At("HEAD"), cancellable = true)
    private void limitAddServer(String motd, java.net.InetAddress address, CallbackInfo ci) {
        if (this.servers.size() >= LancutterConfig.maxLanServers) {
            this.isDirty = true;
            ci.cancel();
        }
    }
}
