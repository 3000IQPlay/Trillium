package dev._3000IQPlay.trillium.gui.classic.components.items.buttons;

import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.gui.classic.ClassicGui;
import dev._3000IQPlay.trillium.gui.fonttwo.fontstuff.FontRender;
import dev._3000IQPlay.trillium.modules.client.ClickGui;
import dev._3000IQPlay.trillium.setting.Parent;
import dev._3000IQPlay.trillium.setting.Setting;
import dev._3000IQPlay.trillium.util.RenderUtil;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class ParentSettingButton extends Button{
    private final Setting<Parent> parentSetting;

    // private final Setting setting;

    public ParentSettingButton(Setting setting) {
        super(setting.getName());
        this.parentSetting = setting;
        this.width = 15;
    }

    private final ResourceLocation logo = new ResourceLocation("textures/parent.png");

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width + 7.4f, this.y + (float) this.height - 0.5f, Trillium.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue());
        FontRender.drawString5(this.getName(), this.x + 2.3f, this.y - 1.7f - (float) ClassicGui.getClickGui().getTextOffset(), -1 );
        mc.getTextureManager().bindTexture(this.logo);
        ModuleButton.drawCompleteImage(this.x - 1.5f + (float) this.width - 7.8f, this.y - 5.0f - (float) ClassicGui.getClickGui().getTextOffset(), 20, 20);
    }

    @Override
    public void update() {
        this.setHidden(!this.parentSetting.isVisible());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovering(mouseX, mouseY)) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            getParentSetting().getValue().setExtended(!getParentSetting().getValue().isExtended());
        }
    }

    @Override
    public float getHeight() {
        return 14;
    }

  //  @Override
  //  public void toggle() {
      //  this.parentSetting.setValue((Boolean) this.parentSetting.getValue() == false);
   // }

  //  @Override
   // public boolean getState() {
     //   return (Boolean) this.setting.getValue();
   // }

    public Setting<Parent> getParentSetting() {
        return parentSetting;
    }

}
