package dev._3000IQPlay.trillium.event;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.gui.auth.AuthGui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventProcessor {
    private final Minecraft mc = Minecraft.getMinecraft();
	
	public EventProcessor() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
    @SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		if (!(event.getGui() instanceof AuthGui) && Trillium.isOpenAuthGui) {
			event.setCanceled(true);
		}
	}

    public void onInit() {
        mc.displayGuiScreen(new AuthGui());
        Trillium.isOpenAuthGui = true;
    }
}