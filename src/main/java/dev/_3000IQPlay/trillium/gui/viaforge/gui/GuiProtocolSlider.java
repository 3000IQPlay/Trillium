package dev._3000IQPlay.trillium.gui.viaforge.gui;

import dev._3000IQPlay.trillium.gui.viaforge.ViaForge;
import dev._3000IQPlay.trillium.gui.viaforge.protocol.ProtocolCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public class GuiProtocolSlider
extends GuiButton {
    public float sliderValue = 1.0f;
    public boolean dragging;
    public float minValue = 0.0f;
    public float maxValue = ProtocolCollection.values().length - 1;

    public GuiProtocolSlider(int n, int n2, int n3, int n4, int n5) {
        super(n, n2, n3, n4, n5, "Protocol");
        for (int i = 0; i < ProtocolCollection.values().length; ++i) {
            if (ProtocolCollection.values()[i].getVersion().getVersion() != ViaForge.getInstance().getVersion()) continue;
            this.sliderValue = (float)i / (float)ProtocolCollection.values().length;
            this.displayString = "Protocol: " + ProtocolCollection.values()[i].getVersion().getName();
        }
    }

    public void mouseReleased(int n, int n2) {
        this.dragging = false;
    }

    public int getHoverState(boolean bl) {
        return 0;
    }

    public void mouseDragged(Minecraft minecraft, int n, int n2) {
        block1: {
            if (!this.visible) break block1;
            if (this.dragging) {
                this.sliderValue = (float)(n - (this.x + 4)) / (float)(this.width - 8);
                this.sliderValue = MathHelper.clamp((float)this.sliderValue, (float)0.0f, (float)1.0f);
                int n3 = (int)(this.sliderValue * this.maxValue);
                ViaForge.getInstance().setVersion(ProtocolCollection.values()[n3].getVersion().getVersion());
                this.displayString = "Protocol: " + ProtocolCollection.values()[n3].getVersion().getName();
            }
            minecraft.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.x + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
        }
    }

    public boolean mousePressed(Minecraft minecraft, int n, int n2) {
        if (super.mousePressed(minecraft, n, n2)) {
            this.sliderValue = (float)(n - (this.x + 4)) / (float)(this.width - 8);
            this.sliderValue = MathHelper.clamp((float)this.sliderValue, (float)0.0f, (float)1.0f);
            int n3 = (int)(this.sliderValue * this.maxValue);
            ViaForge.getInstance().setVersion(ProtocolCollection.values()[n3].getVersion().getVersion());
            this.displayString = "Protocol: " + ProtocolCollection.values()[n3].getVersion().getName();
            this.dragging = true;
            return true;
        }
        return false;
    }
}