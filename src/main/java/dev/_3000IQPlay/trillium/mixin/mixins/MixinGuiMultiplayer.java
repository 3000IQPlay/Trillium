package dev._3000IQPlay.trillium.mixin.mixins;


import dev._3000IQPlay.trillium.modules.client.MultiConnect;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiMultiplayer.class)
public abstract class MixinGuiMultiplayer extends GuiScreen {
	
    @Inject(method = "createButtons", at = @At("HEAD"))
    public void dobovlyaemhuiny(CallbackInfo ci) {
        if(MultiConnect.getInstance().isEnabled()) {
            IGuiScreen screen = (IGuiScreen) (GuiScreen) (Object) this;

            List<GuiButton> buttonList = screen.getButtonList();
            buttonList.add(new GuiButton(22810007, ((GuiScreen) (Object) this).width / 2 + 4 + 76 + 95, this.height - 52, 98, 20, "MultiConnect"));
            buttonList.add(new GuiButton(1337339, ((GuiScreen) (Object) this).width / 2 + 4 + 76 + 95, this.height - 28, 98, 20, "Clear Selected"));
            screen.setButtonList(buttonList);
        }
    }

    @Inject(method = "actionPerformed", at = @At(value = "RETURN"))
    public void chekarmknopki(GuiButton button, CallbackInfo ci) {
        if(MultiConnect.getInstance().isEnabled()) {
            if (button.id == 1337339) {
                MultiConnect.getInstance().serverData.clear();
            }

            if (button.id == 22810007) {
                if (!MultiConnect.getInstance().serverData.isEmpty()) {
                    for (int serv : MultiConnect.getInstance().serverData) {
                        connectToSelected(serv);
                    }
                } else {
                    System.out.println("Trillum - Chose Server");
                }
            }
        }
    }

    @Shadow
    private ServerSelectionList serverListSelector;

    public void connectToSelected(int pizda) {
        if(MultiConnect.getInstance().isEnabled()) {
            GuiListExtended.IGuiListEntry guilistextended$iguilistentry = pizda < 0 ? null : serverListSelector.getListEntry(pizda);
            if (guilistextended$iguilistentry instanceof ServerListEntryNormal) {
                FMLClientHandler.instance().connectToServer(this, ((ServerListEntryNormal) guilistextended$iguilistentry).getServerData());
            }
        }
    }
}