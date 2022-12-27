package dev._3000IQPlay.trillium.modules.client;

import dev._3000IQPlay.trillium.modules.Module;
import io.netty.buffer.Unpooled;
import dev._3000IQPlay.trillium.event.events.PacketEvent;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class FakeVanilla
        extends Module {
    public FakeVanilla() {
        super("FakeVanilla", "не отправляет модлист-серверу", Module.Category.CLIENT, true, false, false);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketCustomPayload packet;
        if (event.getPacket() instanceof FMLProxyPacket && !mc.isSingleplayer()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketCustomPayload && (packet = event.getPacket()).getChannelName().equals("MC|Brand")) {
            packet.data = new PacketBuffer(Unpooled.buffer()).writeString("vanilla");
        }
    }
}

