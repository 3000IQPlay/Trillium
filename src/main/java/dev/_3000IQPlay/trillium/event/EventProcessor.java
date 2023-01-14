package dev._3000IQPlay.trillium.event;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.gui.auth.AuthGui;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventProcessor {
    private final Minecraft mc = Minecraft.getMinecraft();
	
    @SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		if (!(event.getGui() instanceof AuthGui) && Trillium.isOpenAuthGui && !Trillium.allowToConfiguredAnotherClients) {
			event.setCanceled(true);
		}
	}

    public void onInit() {
        if (Trillium.allowToConfiguredAnotherClients) return;
        mc.displayGuiScreen(new AuthGui());
        Trillium.isOpenAuthGui = true;
    }
}