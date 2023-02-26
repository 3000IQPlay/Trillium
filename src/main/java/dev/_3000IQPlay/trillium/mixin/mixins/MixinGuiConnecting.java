package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.ConnectToServerEvent;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = GuiConnecting.class, priority = 999)
public class MixinGuiConnecting extends MixinGuiScreen {

    @Inject(method = {"connect"}, at = {@At(value = "HEAD")})
    private void  connectHook(String ip, int port, CallbackInfo ci){
        Trillium.ServerIp = ip;
        Trillium.ServerPort = port;

        ConnectToServerEvent event = new ConnectToServerEvent(ip);
        MinecraftForge.EVENT_BUS.post(event);
    }
}