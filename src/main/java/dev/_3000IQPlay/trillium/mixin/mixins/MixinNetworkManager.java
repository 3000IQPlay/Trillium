package dev._3000IQPlay.trillium.mixin.mixins;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.util.MovementUtil;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static dev._3000IQPlay.trillium.util.ItemUtil.mc;

@Mixin(value = {NetworkManager.class})
public class MixinNetworkManager {

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onSendPacketPre(Packet<?> packet, CallbackInfo info) {
        PacketEvent.Send event = new PacketEvent.Send(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = {"channelRead0"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onChannelReadPre(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        PacketEvent.Receive event = new PacketEvent.Receive(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }else if (!event.getPostEvents().isEmpty())
        {

            for (Runnable runnable : event.getPostEvents())
            {
                Minecraft.getMinecraft().addScheduledTask(runnable);
            }

            info.cancel();
        }
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At(value = "RETURN")}, cancellable = true)
    private void onSendPacketPost(Packet<?> packet, CallbackInfo info) {
        PacketEvent.SendPost event = new PacketEvent.SendPost(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }


    @Inject(method = {"channelRead0"}, at = {@At(value = "RETURN")}, cancellable = true)
    private void onChannelReadPost(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        PacketEvent.ReceivePost event = new PacketEvent.ReceivePost(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }

    @Inject(method = {"sendPacket(Lnet/minecraft/network/Packet;)V"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onSendPacketPost2(Packet<?> packet, CallbackInfo info) {
        PacketEvent.SendPost event = new PacketEvent.SendPost(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }


    @Inject(method = {"channelRead0"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onChannelReadPost2(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        PacketEvent.ReceivePost event = new PacketEvent.ReceivePost(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            info.cancel();
        }
    }
}