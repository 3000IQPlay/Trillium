package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import net.minecraft.client.gui.GuiScreen;

public class GuiOpenEvent extends EventStage {
    private GuiScreen gui;
    public GuiOpenEvent(GuiScreen gui) {
        this.setGui(gui);
    }

    public GuiScreen getGui() {
        return gui;
    }

    public void setGui(GuiScreen gui) {
        this.gui = gui;
    }
}