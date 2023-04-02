package dev._3000IQPlay.trillium.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev._3000IQPlay.trillium.Trillium;
import dev._3000IQPlay.trillium.command.Command;
import dev._3000IQPlay.trillium.event.events.ClientEvent;
import dev._3000IQPlay.trillium.gui.mainmenu.GLSLShaderList;
import dev._3000IQPlay.trillium.modules.Module;
import dev._3000IQPlay.trillium.setting.Setting;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MainSettings extends Module {
    private static MainSettings INSTANCE = new MainSettings();
	public Setting<Boolean> mainMenuShader = this.register(new Setting<Boolean>("MainMenuShader", true));
	public Setting<Integer> shaderFPS = this.register(new Setting<Integer>("ShaderFPS", 60, 5, 60, v -> this.mainMenuShader.getValue()));
	public Setting<Boolean> randomShader = this.register(new Setting<Boolean>("RandomShader", true, v -> this.mainMenuShader.getValue()));
	public Setting<GLSLShaderList> menuShader = this.register(new Setting<GLSLShaderList>("MenuShader", GLSLShaderList.CoolBlob, v -> this.mainMenuShader.getValue() && !this.randomShader.getValue()));

    public Setting<Boolean> notifyToggles = this.register(new Setting<Boolean>("NotifyToggles", false));
	public Setting<Boolean> customFov = this.register(new Setting<Boolean>("CustomFov", false));
	public Setting<Float> fov = this.register(new Setting<Float>("Fov", 125.0f, 0.0f, 300.0f, v -> this.customFov.getValue()));
	
	public MainSettings() {
        super("MainSettings", "Client settings", Module.Category.CLIENT, true, false, false);
    }
	
	public static MainSettings getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainSettings();
        }
        return INSTANCE;
    }
	
	@Override
	public void onUpdate() {
	    if (this.customFov.getValue().booleanValue()) {
            MainSettings.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fov.getValue().floatValue());
        }
	}
}