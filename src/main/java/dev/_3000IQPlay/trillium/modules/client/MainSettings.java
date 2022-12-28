package dev._3000IQPlay.trillium.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.ClientEvent;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MainSettings extends Module {
    public MainSettings() {
        super("MainSettings", "Client settings", Category.CLIENT, true, false, false);
    }


    public Setting<String> prefix = this.register(new Setting<String>("Prefix", "."));
    public Setting<Boolean> notifyToggles = this.register(new Setting<Boolean>("NotifyToggles", false));



    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                Trillium.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Установлен префикс " + ChatFormatting.DARK_GRAY + Trillium.commandManager.getPrefix());
            }
        }
    }
}
