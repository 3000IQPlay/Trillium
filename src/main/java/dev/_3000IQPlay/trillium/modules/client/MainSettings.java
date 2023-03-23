package dev._3000IQPlay.trillium.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.event.events.ClientEvent;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MainSettings extends Module {
    public MainSettings() {
        super("MainSettings", "Client settings", Module.Category.CLIENT, true, false, false);
    }

    public Setting<Boolean> notifyToggles = this.register(new Setting<Boolean>("NotifyToggles", false));
	public Setting<Boolean> customFov = this.register(new Setting<Boolean>("CustomFov", false));
	public Setting<Float> fov = this.register(new Setting<Float>("Fov", 125.0f, 0.0f, 300.0f, v -> this.customFov.getValue()));
	
	@Override
	public void onUpdate() {
	    if (this.customFov.getValue().booleanValue()) {
            MainSettings.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fov.getValue().floatValue());
        }
	}
}
