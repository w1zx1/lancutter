package net.w1zx1.lancutter.mixin;

import net.minecraft.client.server.LanServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(targets = "net.minecraft.client.server.LanServerDetection$LanServerList")
public interface LanServerListAccessor {

    @Accessor("servers")
    List<LanServer> getServers();
}
