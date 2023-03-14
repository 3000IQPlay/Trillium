package dev._3000IQPlay.trillium.modules.player;

import dev._3000IQPlay.trillium.event.events.PacketEvent;
import dev._3000IQPlay.trillium.modules.Module;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XCarry
        extends Module {
	public XCarry() {
        super("XCarry", "Allows you to store items in your crafting inventory and drag slot", Module.Category.PLAYER, false, false, false);
    }
	
	@SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketCloseWindow) {
            event.setCanceled(true);
        }
    }
	
	@Override
    public void onDisable() {
        if (!XCarry.nullCheck()) {
            mc.player.connection.sendPacket(new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
        }
    }
}